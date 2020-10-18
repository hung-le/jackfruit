package com.hungle.jacktrip.jackfruit;

public interface Authenticator {

    ConnectionResult authenticate(String hostName, String userName, String password);

}
