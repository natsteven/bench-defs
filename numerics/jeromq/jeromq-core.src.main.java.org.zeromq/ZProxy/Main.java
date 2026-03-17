

/** filtered and transformed by ARG-V */

import org.sosy_lab.sv_benchmarks.Verifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of a remotely controlled  proxy for 0MQ, using {@link ZActor}.
 * <br>
 * The goals of this implementation are to delegate the creation of sockets
 * in a background thread via a callback interface to ensure their correct use
 * and to provide ultimately to end-users the following features.
 *
 * <p>Basic features:</p>
 * <ul>
 *  <li>Remote Control
 *   <ul>
 *   <li>Start:                                 <i>if was paused, flushes the pending messages</i>
 *   <li>Pause:                                 <b><i>lets the socket queues accumulate messages according to their types</i></b>
 *   <li>Stop:                                  <i>Shutdowns the proxy, can be restarted</i>
 *   <li>Status:                                <i>Retrieves the status of the proxy</i>
 *   <li>Cold Restart:                          <i>Closes and recreates the connections</i>
 *   <li>{@link #restart(ZMsg) Hot Restart}:    <i>User-defined behavior with custom messages</i>
 *   <li>{@link #configure(ZMsg) Configure}:    <i>User-defined behavior with custom messages</i>
 *   <li>{@link #command(String, boolean)} ...: <i>Custom commands of your own</i>
 *   <li>Exit:                                  <i>Definitive shutdown of the proxy and its control</i>
 *  </ul>
 *  All the non-custom commands can be performed in asynchronous or synchronous mode.
 *  <br>
 * <li>Proxy mechanism ensured by pluggable pumps
 *    <ul>
 *      <li>with built-in low-level {@link org.zeromq.ZProxy.ZmqPump} (zmq.ZMQ): useful for performances
 *      <li>with built-in high-level  {@link org.zeromq.ZProxy.ZPump}  (ZeroMQ): useful for {@link org.zeromq.ZProxy.ZPump.Transformer message transformation}, lower performances
 *      <li>with your own-custom proxy pump implementing a {@link Pump 1-method interface}
 *    </ul>
 * </ul><p>
 * <br>
 * You can have all the above non-customizable features in about these lines of code:
 * <pre>
 * {@code
        final ZProxy.Proxy provider = new ZProxy.SimpleProxy()
        {
            public Socket create(ZContext ctx, ZProxy.Plug place, Object ... args)
            {
                assert ("TEST".equals(args[0]);
                Socket socket = null;
                if (place == ZProxy.Plug.FRONT) {
                    socket = ctx.createSocket(ZMQ.ROUTER);
                }
                if (place == ZProxy.Plug.BACK) {
                    socket = ctx.createSocket(ZMQ.DEALER);
                }
                return socket;
            }

            public void configure(Socket socket, ZProxy.Plug place, Object ... args)
            {
                assert ("TEST".equals(args[0]);
                int port = -1;
                if (place == ZProxy.Plug.FRONT) {
                    port = socket.bind("tcp://127.0.0.1:6660");
                }
                if (place == ZProxy.Plug.BACK) {
                    port = socket.bind("tcp://127.0.0.1:6661");
                }
                if (place == ZProxy.Plug.CAPTURE && socket != null) {
                    socket.bind("tcp://127.0.0.1:4263");
                }
            }
        };

        ZProxy proxy = ZProxy.newProxy("ProxyOne", provider, "ABRACADABRA", Arrays.asList("TEST"));
}
 * </pre>
 * Once created, the proxy is not started. You have to perform first a start command on it.
 * This choice was made because it is easier for a user to start it with one line of code than for the code to internally handle
 * different possible starting states (after all, someone may want the proxy started but paused at first or configured in a specific way?)
 * and because the a/sync stuff was funnier. Life is unfair ...
 * Or maybe an idea is floating in the air?
 * <br>
 * You can then use it like this:
 * <pre>
 * {@code
        final boolean async = false, sync = true;
        String status = null;
        status = proxy.status();
        status = proxy.pause(sync);
        status = proxy.start(async);
        status = proxy.restart(new ZMsg());
        status = proxy.status(async);
        status = proxy.stop(sync);
        boolean here = proxy.sign();
        ZMsg cfg = new ZMsg();
        msg.add("CONFIG-1");
        ZMsg rcvd = proxy.configure(cfg);
        proxy.exit();
        status = proxy.status(sync);
        assert (!proxy.started());
   }
 * </pre>
 *
 * A {@link #command(Command, boolean) programmatic interface} with enums is also available.
 *
 *
 */
// Proxy for 0MQ.
public class Main
{
    /**
     * Possible places for sockets in the proxy.
     */
    public enum Plug
    {
        FRONT, // The position of the frontend socket.
        BACK, // The position of the backend socket.
        CAPTURE // The position of the capture socket.
    }

    /**
     * Starts the proxy.
     *
     * @param sync true to read the status in synchronous way, false for asynchronous mode
     * @return the read status
     */
    public String start(boolean sync)
    {
        assert true; //inline assert generated by ARG-V
		return command(START, sync);
    }

    /**
     * Pauses the proxy.
     * A paused proxy will cease processing messages, causing
     * them to be queued up and potentially hit the high-water mark on the
     * frontend or backend socket, causing messages to be dropped, or writing
     * applications to block.
     *
     * @param sync     true to read the status in synchronous way, false for asynchronous mode
     * @return the read status
     */
    public String pause(boolean sync)
    {
        assert true; //inline assert generated by ARG-V
		return command(PAUSE, sync);
    }

    /**
     * Stops the proxy.
     *
     * @param sync     true to read the status in synchronous way, false for asynchronous mode
     * @return the read status
     */
    public String stop(boolean sync)
    {
        assert true; //inline assert generated by ARG-V
		return command(STOP, sync);
    }

    /**
     * Sends a command message to the proxy actor.
     * Can be useful for programmatic interfaces.
     * Does not works with commands {@link #CONFIG CONFIG} and {@link #RESTART RESTART}.
     *
     * @param command  the command to execute. Not null.
     * @param sync     true to read the status in synchronous way, false for asynchronous mode
     * @return the read status
     */
    /** ARG-V: suitable */
	 public String command(String command, boolean sync)
    {
        if (STATUS.equals(command)) {
            assert true; //inline assert generated by ARG-V
			return status(sync);
        }
        if (EXIT.equals(command)) {
            assert true; //inline assert generated by ARG-V
			return exit();
        }
        // consume the status in the pipe
        String status = recvStatus();

        if (Verifier.nondetBoolean()) {
            // the pipe is refilled
            if (sync) {
                status = status(true);
            }
        }
        assert true; //inline assert generated by ARG-V
		return status;
    }

    /**
     * Stops the proxy and exits.
     *
     * @param sync     forced to true to read the status in synchronous way.
     * @return the read status.
     * @deprecated The call is synchronous: the sync parameter is ignored,
     * as it leads to many mistakes in case of a provided ZContext.
     */
    public String exit(boolean sync)
    {
        assert true; //inline assert generated by ARG-V
		return exit();
    }

    /**
     * Stops the proxy and exits.
     * The call is synchronous.
     *
     * @return the read status.
     *
     */
    public String exit()
    {
        assert true; //inline assert generated by ARG-V
		return Verifier.nondetString();
    }

    /**
     * Inquires for the status of the proxy.
     * This call is synchronous.
     */
    public String status()
    {
        assert true; //inline assert generated by ARG-V
		return status(true);
    }

    /**
     * Inquires for the status of the proxy.
     *
     * @param sync     true to read the status in synchronous way, false for asynchronous mode.
     * If false, you get the last cached status of the proxy
     */
    public String status(boolean sync)
    {
        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return Verifier.nondetString();
        }
        try {
            String status = recvStatus();

            assert true; //inline assert generated by ARG-V
			return status;
        }
        catch (Exception e) {
            assert true; //inline assert generated by ARG-V
			return Verifier.nondetString();
        }
    }

    // receives the last known state of the proxy
    private String recvStatus()
    {
        if (Verifier.nondetBoolean()) {
            assert true; //inline assert generated by ARG-V
			return Verifier.nondetString();
        }

        String status = Verifier.nondetString();
        assert true; //inline assert generated by ARG-V
		return status;
    }

    /**
     * Binary inquiry for the status of the proxy.
     */
    public boolean isStarted()
    {
        assert true; //inline assert generated by ARG-V
		return started();
    }

    /**
     * Binary inquiry for the status of the proxy.
     */
    public boolean started()
    {
        String status = status(true);
        assert true; //inline assert generated by ARG-V
		return STARTED.equals(status);
    }

    // to handle commands in a more java-centric way
    public enum Command
    {
        START,
        PAUSE,
        STOP,
        RESTART,
        EXIT,
        STATUS,
        CONFIG
    }

    // commands for the control pipe
    private static String START   = Command.START.name();
    private static String PAUSE   = Command.PAUSE.name();
    private static String STOP    = Command.STOP.name();
    private static String RESTART = Command.RESTART.name();
    private static String EXIT    = Command.EXIT.name();
    private static String STATUS  = Command.STATUS.name();
    private static String CONFIG  = Command.CONFIG.name();

    // to handle states in a more java-centric way
    public enum State
    {
        ALIVE,
        STARTED,
        PAUSED,
        STOPPED,
        EXITED
    }

    // state responses from the control pipe
    public static String STARTED = State.STARTED.name();
    public static String PAUSED  = State.PAUSED.name();
    public static String STOPPED = State.STOPPED.name();
    public static String EXITED  = State.EXITED.name();
    // defines the very first time where no command changing the state has been issued
    public static String ALIVE = State.ALIVE.name();

    private static AtomicInteger counter = new AtomicInteger();

	/** This main was generated by ARG-V */
	
	public static void main(String[] args) throws Exception {
		Main instance = new Main();
		instance.start(Verifier.nondetBoolean());
		instance.pause(Verifier.nondetBoolean());
		instance.stop(Verifier.nondetBoolean());
		instance.command(Verifier.nondetString(), Verifier.nondetBoolean());
		instance.exit(Verifier.nondetBoolean());
		instance.exit();
		instance.status();
		instance.status(Verifier.nondetBoolean());
		instance.recvStatus();
		instance.isStarted();
		instance.started();
	}
}
