package com.icod.ilearning.services.handler;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class UserActor extends AbstractActor {

    final ActorSystem actorSystem;

    private UserActor(){
        actorSystem = context().system();
    }

    public static Props props(){
        return Props.create(UserActor.class,()-> new UserActor());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
