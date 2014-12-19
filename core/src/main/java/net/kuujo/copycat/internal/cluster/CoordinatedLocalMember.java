/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.copycat.internal.cluster;

import net.kuujo.copycat.Managed;
import net.kuujo.copycat.cluster.LocalMember;
import net.kuujo.copycat.cluster.MessageHandler;
import net.kuujo.copycat.cluster.coordinator.LocalMemberCoordinator;
import net.kuujo.copycat.spi.ExecutionContext;

import java.util.concurrent.CompletableFuture;

/**
 * Internal local cluster member.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class CoordinatedLocalMember extends CoordinatedMember implements LocalMember, Managed {
  private final LocalMemberCoordinator coordinator;

  public CoordinatedLocalMember(int id, LocalMemberCoordinator coordinator, ExecutionContext context) {
    super(id, coordinator, context);
    this.coordinator = coordinator;
  }

  @Override
  public <T, U> LocalMember registerHandler(String topic, MessageHandler<T, U> handler) {
    coordinator.register(topic, id, handler);
    return this;
  }
  
  @Override
  public LocalMember unregisterHandler(String topic) {
    coordinator.unregister(topic, id);
    return this;
  }

  @Override
  public CompletableFuture<Void> open() {
    coordinator.registerExecutor(id, context);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Void> close() {
    coordinator.unregisterExecutor(id);
    return CompletableFuture.completedFuture(null);
  }
}