package com.goorm.profileboxapiuser.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_NAME = "my-topic";

    public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

//    public void sendMessage(String message) {
//        kafkaTemplate.send(TOPIC_NAME, message);
//        System.out.println("Sent message: " + message);
//    }


//    public void sendMessageFindLoginMemberByEmail(String message) {
//        kafkaTemplate.send(TOPIC_NAME, message);
//        System.out.println("Sent message: " + message);
//    }
//
//    @KafkaListener(topics = TOPIC_NAME)
//    public void consumeMessageFindLoginMemberByEmail(String message) {
//        System.out.println("User API Received message: " + message);
//        Member member = new Member();
//        completableFuture.complete(member);
//    }


    //        userDto.setOrders(orderList);


    //        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, email);
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                System.out.println("Sent message=[" + email +
//                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
//            } else {
//                System.out.println("Unable to send message=[" +
//                        email + "] due to : " + ex.getMessage());
//            }
//        });
//        kafkaTemplate.flush();
//
//        // 응답을 대기
//        SendResult<String, String> message = future.get();

    //----
//        CompletableFuture<Member> completableFuture = new CompletableFuture<>();
//
//        sendMessageFindLoginMemberByEmail(email);
//
//        kafkaTemplate.flush();
//
//        kafkaTemplate.sendOffsetsToTransaction(kafkaTemplate.getProducerFactory().createProducer().partitionsFor(TOPIC_NAME)
//                .stream()
//                .collect(Collectors.toMap(TopicPartitionInfo::topic, tp -> new OffsetAndMetadata(tp.partition(), 0L))), "groupId");
//
//        kafkaTemplate.executeInTransaction(operations -> {
//            try {
//                Member member = completableFuture.get();
//                return member;
//            } catch (InterruptedException | ExecutionException e) {
//                // 예외 처리
//                e.printStackTrace();
//                return null;
//            }
//        });
//
//        // completableFuture에서 결과를 기다림
//        return completableFuture.join();

    // -----

////        return memberRepository.findMemberByMemberEmail(email)
////                .orElseThrow(() -> new ApiException(ExceptionEnum.LOGIN_FAILED));

//        sendMessageFindLoginMemberByEmail(email);
//        Member member = new Member();
//        return member;
}


