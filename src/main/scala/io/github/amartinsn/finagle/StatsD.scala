/*
 * Copyright (c) 2015 Alex Martins.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.amartinsn.finagle

import com.twitter.finagle.{Name, Service, ServiceFactory, Stack}
import com.twitter.finagle.client.{StackClient, StdStackClient, Transporter}
import com.twitter.finagle.dispatch.SerialClientDispatcher
import com.twitter.finagle.transport.Transport
import org.jboss.netty.channel.{ChannelPipelineFactory, Channels, SimpleChannelDownstreamHandler}

object StatsD extends com.twitter.finagle.Client[String, Unit] {

  case class Client(
       stack: Stack[ServiceFactory[String, Unit]] = StackClient.newStack,
       params: Stack.Params = StackClient.defaultParams)
    extends StdStackClient[String, Unit, Client] {

    protected type In  = String
    protected type Out = Unit

    protected def copy1(
       stack: Stack[ServiceFactory[In, Out]],
       params: Stack.Params): Client =
      copy(stack, params)

    override protected def newTransporter(): Transporter[In, Out] = ???
    //    protected def newTransporter(): Transporter[In, Out] =
    //      Netty3Transporter(new NioDatagramChannelFactory(), params)

    protected def newDispatcher(
       transport: Transport[In, Out]): Service[In, Out] =
      new SerialClientDispatcher(transport)
  }

  override def newClient(dest: Name, label: String): ServiceFactory[String, Unit] = ???
}

/**
 * A Netty3 pipeline that is responsible for framing network
 * traffic in terms of logical packets.
 */
object StatsDClientPipelineFactory extends ChannelPipelineFactory {
  def getPipeline = {
    val pipeline = Channels.pipeline()
    // Do we need a Decoder here? Since it's a fire-and-forget request?
    pipeline.addLast("packetEncoder", new PacketEncoder)
    pipeline
  }
}

class PacketEncoder extends SimpleChannelDownstreamHandler {
//  def writeRequested(ctx: ChannelHandlerContext, evt: MessageEvent) =
//    evt.getMessage match {
//      case p: Packet =>
//        try {
//          val cb = p.toChannelBuffer
//          Channels.write(ctx, evt.getFuture, cb, evt.getRemoteAddress)
//        } catch {
//          case NonFatal(e) =>
//            evt.getFuture.setFailure(new ChannelException(e.getMessage))
//        }
//
//      case unknown =>
//        evt.getFuture.setFailure(new ChannelException(
//          "Unsupported request type %s".format(unknown.getClass.getName)))
//    }
}