package com.drinkpebble.uber

import com.uber.sdk.rides.client.ServerTokenSession
import com.uber.sdk.rides.client.SessionConfiguration
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class ApplicationServer extends Application<ApplicationConfiguration>{
    static void main (String[] args) throws Exception{
        new ApplicationServer().run(args)
    }

    @Override
    void initialize(Bootstrap<ApplicationConfiguration> bootstrap){
    }

    @Override
    void run(ApplicationConfiguration configuration, Environment environment){
        SessionConfiguration config = new SessionConfiguration.Builder()
        .setClientId(configuration.uberClientId)
        .setServerToken(configuration.uberServerToken)
        .build()


        ServerTokenSession session = new ServerTokenSession(config)

        environment.jersey().register(new UberRequestResource(session, configuration))
    }
}
