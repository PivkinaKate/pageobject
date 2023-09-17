package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPageV2;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

public class MoneyTransferTest {

  DashboardPage dashboardPage;

  @BeforeEach
  void setup() {
    var loginPage = open("http://localhost:9999", LoginPageV2.class);
    var authInfo = getAuthInfo();
    var verificationPage = loginPage.validLogin(authInfo);
    var verificationCode = getVerificationCode(authInfo);
    dashboardPage = verificationPage.validVerify(verificationCode);
  }




  @Test
    void shouldTransferMoneyBetweenCards() {
      var firstCardInfo = getFirstCardInfo();
      var secondCardInfo = getSecondCardInfo();
      var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
      var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
      var amount = generateValidAmount(firstCardBalance);
      var expectedBalanceFirstCard = firstCardBalance - amount;
      var expectedBalanceSecondCard = secondCardBalance + amount;
      var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
      dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
      var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
      var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
      assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
      assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }

  @Test
  void shouldTransferMoneyBetweenOwnCardsV2() {
    var firstCardInfo = getFirstCardInfo();
    var secondCardInfo = getSecondCardInfo();
    var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
    var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    var amount = generateInvalidAmount(secondCardBalance);
    var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
    transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
    transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
    var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
    var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
    assertEquals(firstCardBalance, actualBalanceFirstCard);
    assertEquals(secondCardBalance, actualBalanceSecondCard);
  }


}

