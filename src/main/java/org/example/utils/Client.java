package org.example.utils;

import java.util.List;
import java.util.Scanner;

public class Client {

    private final MenuApiClient menuApiClient = new MenuApiClient();
    private final PaymentApiClient paymentApiClient = new PaymentApiClient();

    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("---식당 메뉴 조회 및 결제---");

        try {

            System.out.print(" 식당 ID 입력 >> ");
            long restaurantId = scanner.nextLong();
            scanner.nextLine();

            List<MenuListResponseDTO> menus = menuApiClient.getMenus(restaurantId);
            // ... (메뉴 출력 로직 생략) ...

            if (menus.isEmpty()) {
                System.out.println("잘못된 ID");
                return;
            }

            System.out.println("\n--- 결제 ---");

            System.out.print("결제할 메뉴 ID  >> ");
            long menuId = scanner.nextLong();
            scanner.nextLine();
            long finalCharge = 5000;

            System.out.println("\n--- 결제 유형 선택 ---");
            System.out.println("1. 카드 결제 (CARD)");
            System.out.println("2. 쿠폰만 사용 (COUPON)");
            System.out.println("3. 쿠폰 사용 후 나머지 금액 카드 결제 (MIXED)");
            System.out.print("선택 (1/2/3) >> ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String purchaseType = null;
            long couponId = 0;

            switch (choice) {
                case 1:
                    purchaseType = "CARD";
                    break;
                case 2:
                    purchaseType = "COUPON";
                    System.out.print("사용할 쿠폰 ID를 입력하세요 >> ");
                    couponId = scanner.nextLong();
                    scanner.nextLine();
                    break;
                case 3:
                    purchaseType = "MIXED";
                    System.out.print("사용할 쿠폰 ID 입력 >> ");
                    couponId = scanner.nextLong();
                    scanner.nextLine();
                    break;
                default:
                    System.out.println("실패");
                    return;
            }

            long userId = 1L;

            String resultMessage = paymentApiClient.sendPaymentRequest(
                    userId, menuId, finalCharge, purchaseType, couponId
            );

            System.out.println("\n 서버 응답: " + resultMessage);

        } catch (java.util.InputMismatchException e) {
            System.out.println("잘못된 형식의 ID");
        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    public static void main(String[] args) {
        new Client().run();
    }
}