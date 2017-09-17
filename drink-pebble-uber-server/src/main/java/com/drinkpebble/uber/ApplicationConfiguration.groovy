package com.drinkpebble.uber

import io.dropwizard.Configuration

class ApplicationConfiguration extends Configuration{
    String uberClientId
    String uberServerToken
    String uberSecret
    String serverUrl
}
