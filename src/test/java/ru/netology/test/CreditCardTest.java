package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.page.PaymentTypesPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditCardTest {

    String approvedCardNumber = DataHelper.getCardApproved().getCardNumber();
    String declinedCardNumber = DataHelper.getCardDeclined().getCardNumber();
    String validMonth = DataHelper.getRandomMonth(1);
    String validYear = DataHelper.getRandomYear(0);
    String validOwnerName = DataHelper.getRandomName();
    String validCode = DataHelper.getNumberCVC(3);

    @BeforeEach
    public void setUp() {
        open("http://localhost:8080");
    }

    @AfterEach
    public void shouldCleanBase() {
        SQLHelper.cleanBase();
    }

    @BeforeAll
    public static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Позитивный сценарий.Оплата в кредит")
    public void shouldCreditPaymentApproved() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.bankApprovedOperation();
        assertEquals("APPROVED", SQLHelper.getCreditPayment());
    }

    @Test
    @DisplayName("Запрещенный номер карты при оплате в кредит")
    // ошибка, банк одобряет операцию, хотя статус Declined
    public void shouldDeclinedCardPayment() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        creditCardPage.fillCardPaymentForm(declinedCardNumber, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.bankDeclinedOperation();
        assertEquals("DECLINED", SQLHelper.getCreditPayment());
    }
    ///НОМЕР КАРТЫ
    @Test
    @DisplayName("Неполный номер карты при оплате в кредит")
    public void shouldHandleInvalidCard() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var invalidCardNumber = DataHelper.GetAShortNumber();
        creditCardPage.fillCardPaymentForm(invalidCardNumber, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата в кредит с незаполненным полем \"Номер карты\"")
    public void shouldHandleEmptyCardNumber() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var emptyCardNumber = DataHelper.getEmptyField();
        creditCardPage.fillCardPaymentForm(emptyCardNumber, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата в кредит с спец.символами в имени")
    public void shouldHandleSpecialSymbolsCardNumber() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var specSymbols = DataHelper.getSpecialSymbols();
        creditCardPage.fillCardPaymentForm(specSymbols, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата в кредит с буквами в имени")
    // ошибка, неверное наименование ошибки. Должно быть "Поле обязательно для заполнения"
    public void shouldHandleLettersInNumber() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var specialLetters = DataHelper.getSpecialLetters();
        creditCardPage.fillCardPaymentForm(specialLetters, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.emptyField();
    }
    @Test
    @DisplayName("Оплата в кредит с валидной картой отсутствующей в базе")
    public void shouldHandNumberNoBase() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var notBase = DataHelper.getCardNoBase();
        creditCardPage.fillCardPaymentForm(notBase, validMonth, validYear, validOwnerName, validCode);
        creditCardPage.bankDeclinedOperation();
    }
    ///МЕСЯЦ
    @Test
    @DisplayName("Оплата в кредит c истекшим месяцем")
    public void shouldHandleExpiredCardMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var monthExpired = DataHelper.getRandomMonth(-1);
        creditCardPage.fillCardPaymentForm(approvedCardNumber, monthExpired, validYear, validOwnerName, validCode);
        creditCardPage.errorCardTermValidity();
    }
    @Test
    @DisplayName("Оплата в кредит с 00 в месяце ")
    //ошибка, операция одобрена, должен быть неверный формат
    public void shouldHandleTwoDigitMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var oneDigital = DataHelper.getInvalidTwo();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, oneDigital, validYear, validOwnerName, validCode);
        creditCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата в кредит с 0 в месяце ")
    public void shouldHandleOneDigitMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var oneDigital = DataHelper.getInvalidOne();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, oneDigital, validYear, validOwnerName, validCode);
        creditCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата в кредит с спец.символами в месяце ")
    //спец символы не вводятся, поэтому ошибка должна быть "Поле обязательно для заполнения"
    public void shouldHandleSpecSymbolsMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var specSymbols = DataHelper.getSpecialSymbols();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, specSymbols, validYear, validOwnerName, validCode);
        creditCardPage.emptyField();
    }
    @Test
    @DisplayName("Оплата в кредит с пустым полем в месяце ")
    // ошибка должна быть "Поле обязательно для заполнения"
    public void shouldHandleEmptyFieldMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var emptyField = DataHelper.getEmptyField();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, emptyField, validYear, validOwnerName, validCode);
        creditCardPage.emptyField();
    }

    ///ГОД
    @Test
    @DisplayName("Оплата в кредит c истекшим годом")
    public void shouldHandleExpiredCardYear() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var expiredYear = DataHelper.getRandomYear(-5);
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, expiredYear, validOwnerName, validCode);
        creditCardPage.termValidityExpired();
    }

    @Test
    @DisplayName("Оплата в кредит с 0 в годе ")
    public void shouldHandleOneDigitYear() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var oneDigital = DataHelper.getInvalidOne();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, oneDigital, validOwnerName, validCode);
        creditCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата в кредит с спец.символами в годе ")
    //спец символы не вводятся, поэтому ошибка должна быть "Поле обязательно для заполнения"
    public void shouldHandleSpecSymbolsYear() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var specSymbols = DataHelper.getSpecialSymbols();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, specSymbols, validOwnerName, validCode);
        creditCardPage.emptyField();
    }
    @Test
    @DisplayName("Оплата в кредит с пустым полем в годе ")
    // ошибка должна быть "Поле обязательно для заполнения"
    public void shouldHandleEmptyFieldYear() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var emptyField = DataHelper.getEmptyField();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, emptyField, validOwnerName, validCode);
        creditCardPage.emptyField();
    }
    ///ВЛАДЕЛЕЦ
    @Test
    @DisplayName("Оплата в кредит c именем на кириллице")
    //ошибка, оплата одобрена, должен быть неверный формат
    public void shouldHandleInvalidOwnerNameRu() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var rusLanguageName = DataHelper.getRandomNameRus();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, rusLanguageName, validCode);
        creditCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата в кредит c цифрами в имени")
    //ошибка, операция одобрена, должен быть неверный формат
    public void shouldHandleInvalidOwnerNameDigits() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var digitsName = DataHelper.getNumberName();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, digitsName, validCode);
        creditCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата в кредит c спец.символами в имени")
    //ошибка, операция одобрена, должен быть неверный формат
    public void shouldHandleInvalidOwnerNameSpecialSymbols() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var specSymbolsName = DataHelper.getSpecialSymbols();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, specSymbolsName, validCode);
        creditCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата в кредит пустым полем имени")
    public void shouldHandleEmptyName() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var emptyName = DataHelper.getEmptyField();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, emptyName, validCode);
        creditCardPage.emptyField();
    }
    ///CVC
    @Test
    @DisplayName("Оплата в кредит c 2 цифрами в  CVC ")
    public void shouldHandleTwoDigitCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var twoDigitCVC = DataHelper.getNumberCVC(2);
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, twoDigitCVC);
        creditCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата в кредит c 1 цифрой в CVC ")
    public void shouldHandleOneDigitCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var oneDigitCVC = DataHelper.getNumberCVC(1);
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, oneDigitCVC);
        creditCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата в кредит c 4 цифрами в CVC ")
    public void shouldHandleFourDigitCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var oneDigitCVC = DataHelper.getNumberCVC(4);
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, oneDigitCVC);
        creditCardPage.bankApprovedOperation();
    }

    @Test
    @DisplayName("Оплата в кредит c пустым поле CVC ")
    //ошибка, в поле Владелец также отображается ошибка "Поле обязательно для заполнения"
    public void shouldHandleEmptyCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var emptyCVC = DataHelper.getEmptyField();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, emptyCVC);
        creditCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата в кредит c спец.символами CVC ")
    //ошибка, в поле Владелец отображается ошибка "Поле обязательно для заполнения"
    public void shouldHandleSpecialSymbolsInCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var specSymbolsCVC = DataHelper.getSpecialSymbols();
        creditCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, specSymbolsCVC);
        creditCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата в кредит все поля пустые")
    public void shouldHandleAllFieldsEmpty() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var creditCardPage = page.creditPayment();
        var emptyField = DataHelper.getEmptyField();
        creditCardPage.fillCardPaymentForm(emptyField, emptyField, emptyField, emptyField, emptyField);
        creditCardPage.errorFormat();
    }
}
