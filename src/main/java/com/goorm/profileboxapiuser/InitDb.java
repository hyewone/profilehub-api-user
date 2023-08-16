//package com.goorm.profileboxapiuser;
//
//import com.goorm.profileboxcomm.entity.Member;
//import com.goorm.profileboxcomm.enumeration.MemberType;
//import com.goorm.profileboxcomm.repository.MemberRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//@RequiredArgsConstructor
//public class InitDb {
//
//    private final InitService initService;
//    private final MemberRepository memberRepository;
//
//    @PostConstruct
//    public void init() {
//        initService.dbInit1();
////        initService.dbInit2();
//    }
//
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    static class InitService {
//
//        private final EntityManager em;
//
//        public void dbInit1() {
//            Member member1 = createMember("userA", "ADMIN","userA@gmail.com", "passwordA", "MALE", "010-1234-5678", new Date());
//            em.persist(member1);
//            Member member2 = createMember("userB", "ACTOR","userB@gmail.com", "passwordB", "FEMALE", "010-9876-5432", new Date());
//            em.persist(member2);
//            Member member3 = createMember("userC", "ADMIN","userC@gmail.com", "passwordC", "MALE", "010-1111-2222", new Date());
//            em.persist(member3);
//            Member member4 = createMember("userD", "ACTOR","userD@gmail.com", "passwordD", "FEMALE", "010-3333-4444", new Date());
//            em.persist(member4);
////            Book book1 = createBook("JPA1 BOOK", 10000, 100);
////            em.persist(book1);
////
////            Book book2 = createBook("JPA2 BOOK", 20000, 100);
////            em.persist(book2);
////
////            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
////            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
////
////            Delivery delivery = createDelivery(member);
////            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
////            em.persist(order);
//        }
//
////        public void dbInit2() {
////            Member member = createMember("userB", "진주", "2", "2222");
////            em.persist(member);
////
////            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
////            em.persist(book1);
////
////            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
////            em.persist(book2);
////
////            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
////            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
////
////            Delivery delivery = createDelivery(member);
////            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
////            em.persist(order);
////        }
////
//        private Member createMember(String memberName, String memberType, String memberEmail, String memberPassword, String memberGender, String memberTelNo, Date memberBirthDt) {
//            Member member = new Member();
//            member.setMemberType(MemberType.valueOf(memberType));
//            member.setMemberName(memberName);
//            member.setMemberEmail(memberEmail);
//            member.setMemberPassword(memberPassword);
//            member.setMemberGender(memberGender);
//            member.setMemberTelNo(memberTelNo);
//            member.setMemberBirthDt(memberBirthDt);
//            return member;
//        }
////
////        private Book createBook(String name, int price, int stockQuantity) {
////            Book book1 = new Book();
////            book1.setName(name);
////            book1.setPrice(price);
////            book1.setStockQuantity(stockQuantity);
////            return book1;
////        }
////
////        private Delivery createDelivery(Member member) {
////            Delivery delivery = new Delivery();
////            delivery.setAddress(member.getAddress());
////            return delivery;
////        }
//    }
//}
