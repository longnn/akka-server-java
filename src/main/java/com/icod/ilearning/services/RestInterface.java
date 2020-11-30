package com.icod.ilearning.services;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import com.icod.ilearning.services.route.ServiceRoute;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.CompletionStage;

public class RestInterface {
    final ActorSystem actorSystem;
    final Config config = ConfigFactory.load();
    final Http http;
    final ServiceRoute serviceRoute;
    public RestInterface(ActorSystem actorSystem){
        this.actorSystem = actorSystem;
        this.http = Http.get(actorSystem);
        this.serviceRoute = new ServiceRoute(actorSystem);
    }

    public CompletionStage<ServerBinding> start(){
        CompletionStage<ServerBinding> binding = http.newServerAt(config.getString("akka.http.host"),config.getInt("akka.http.port")).bind(serviceRoute.create());
        return binding;
    }
}
