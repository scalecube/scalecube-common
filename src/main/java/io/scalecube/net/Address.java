package io.scalecube.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Address {

  public static final Address NULL_ADDRESS = Address.create("nullhost", 0);

  public static final Pattern ADDRESS_FORMAT = Pattern.compile("(?<host>^.*):(?<port>\\d+$)");

  private String host;
  private int port;

  /** Instantiates empty address for deserialization purpose. */
  Address() {}

  private Address(String host, int port) {
    this.host = convertIfLocalhost(host);
    this.port = port;
  }

  /**
   * Parses given host:port string to create Address instance.
   *
   * @param hostandport must come in form {@code host:port}
   */
  public static Address from(String hostandport) {
    if (hostandport == null || hostandport.isEmpty()) {
      throw new IllegalArgumentException("host-and-port string must be present");
    }

    Matcher matcher = ADDRESS_FORMAT.matcher(hostandport);
    if (!matcher.find()) {
      throw new IllegalArgumentException("can't parse host-and-port string from: " + hostandport);
    }

    String host = matcher.group(1);
    if (host == null || host.isEmpty()) {
      throw new IllegalArgumentException("can't parse host from: " + hostandport);
    }

    int port;
    try {
      port = Integer.parseInt(matcher.group(2));
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("can't parse port from: " + hostandport, ex);
    }

    return new Address(host, port);
  }

  /**
   * Create address from host and port.
   *
   * @param host host
   * @param port port
   * @return address
   */
  public static Address create(String host, int port) {
    return new Address(host, port);
  }

  /**
   * Getting local IP address by the address of local host. <b>NOTE:</b> returned IP address is
   * expected to be a publicly visible IP address.
   *
   * @throws RuntimeException wrapped {@link UnknownHostException}
   */
  public static InetAddress getLocalIpAddress() {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks whether given host string is one of localhost variants. i.e. {@code 127.0.0.1}, {@code
   * 127.0.1.1} or {@code localhost}, and if so - then node's public IP address will be resolved and
   * returned.
   *
   * @param host host string
   * @return local ip address if given host is localhost
   */
  private static String convertIfLocalhost(String host) {
    String result;
    switch (host) {
      case "localhost":
      case "127.0.0.1":
      case "127.0.1.1":
        result = getLocalIpAddress().getHostAddress();
        break;
      default:
        result = host;
    }
    return result;
  }

  /**
   * Returns host.
   *
   * @return host
   */
  public String host() {
    return host;
  }

  /**
   * Returns port.
   *
   * @return port
   */
  public int port() {
    return port;
  }

  /**
   * Returns new address instance with the specified port.
   *
   * @param port port
   * @return address instance
   */
  public Address port(int port) {
    return Address.create(host, port);
  }

  /**
   * Returns new address instance with applied port offset.
   *
   * @param portOffset portOffset
   * @return address instance
   */
  public Address addPortOffset(int portOffset) {
    return Address.create(host, port + portOffset);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Address that = (Address) other;
    return Objects.equals(host, that.host) && Objects.equals(port, that.port);
  }

  @Override
  public int hashCode() {
    return Objects.hash(host, port);
  }

  @Override
  public String toString() {
    return host + ":" + port;
  }
}
