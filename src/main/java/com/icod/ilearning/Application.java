package com.icod.ilearning;

import akka.actor.ActorSystem;
import akka.actor.CoordinatedShutdown;
import akka.http.javadsl.ServerBinding;
import com.icod.ilearning.services.RestInterface;
import com.icod.ilearning.util.Snowflake;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.CompletionStage;

public class Application {

    final Config config = ConfigFactory.load();
    final ActorSystem actorSystem;
    final RestInterface restInterface;
    public static Snowflake idGen;

    public Application(){
        this.idGen = new Snowflake(config.getInt("server.node.id"));
        this.actorSystem = ActorSystem.create(config.getString("akka.system"));
        this.restInterface = new RestInterface(actorSystem);
    }

    public void start(){
        CompletionStage<ServerBinding> binding = this.restInterface.start();
        binding.whenComplete((serverBinding, throwable) -> {
           if(throwable!=null) actorSystem.terminate();
            System.out.println("SERVICE RUNNING AT: "+serverBinding.localAddress().getHostName()+":"+serverBinding.localAddress().getPort());
        });
        CoordinatedShutdown.get(actorSystem).addJvmShutdownHook(()-> System.out.println("APPLICATION SHUTDOWN"));
    }

    public static void main(String[] args) {
        new Application().start();
    }
}
