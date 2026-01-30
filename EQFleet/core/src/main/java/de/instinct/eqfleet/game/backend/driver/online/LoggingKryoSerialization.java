package de.instinct.eqfleet.game.backend.driver.online;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.KryoSerialization;

import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class LoggingKryoSerialization extends KryoSerialization {
	
	private final KryoSerialization delegate = new KryoSerialization();

	@Override
	public void write(Connection connection, ByteBuffer buffer, Object object) {
		delegate.write(connection, buffer, object);
	}

	public Object read(Connection connection, ByteBuffer buffer) {
		// Snapshot buffer state without consuming it.
		final int pos = buffer.position();
		final int remaining = buffer.remaining();

		// Dump first N bytes.
		final int dumpLen = Math.min(64, remaining);
		final String hex = hexPreview(buffer, pos, dumpLen);

		Logger.log("NetWire",
				"TCP frame bytes: remaining=" + remaining + ", first=" + dumpLen + "B: " + hex,
				ConsoleColor.YELLOW);

		try {
			Object obj = delegate.read(connection, buffer);
			if (!(obj instanceof FrameworkMessage)) {
				Logger.log("NetWire", "Decoded object: " + (obj == null ? "null" : obj.getClass().getName()),
						ConsoleColor.CYAN);
			}
			return obj;
		} catch (Exception e) {
			Logger.log("NetWire",
					"Decode failed. remaining=" + remaining + ", first=" + dumpLen + "B: " + hex + " error=" + e,
					ConsoleColor.RED);
			throw e;
		}
	}

	@Override
	public int getLengthLength() {
		return delegate.getLengthLength();
	}

	@Override
	public void writeLength(ByteBuffer buffer, int length) {
		delegate.writeLength(buffer, length);
	}

	@Override
	public int readLength(ByteBuffer buffer) {
		return delegate.readLength(buffer);
	}

	private static String hexPreview(ByteBuffer buffer, int startPos, int len) {
		StringBuilder sb = new StringBuilder(len * 3);
		for (int i = 0; i < len; i++) {
			int v = buffer.get(startPos + i) & 0xFF;
			if (i > 0) sb.append(' ');
			sb.append(Character.forDigit((v >>> 4) & 0xF, 16));
			sb.append(Character.forDigit(v & 0xF, 16));
		}
		return sb.toString();
	}

}
