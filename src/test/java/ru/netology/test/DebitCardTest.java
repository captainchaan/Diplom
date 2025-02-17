package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.page.PaymentTypesPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DebitCardTest {
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
    @DisplayName("Позитивный сценарий.Оплата картой")
    public void shouldCreditPaymentApproved() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, validCode);
        debitCardPage.bankApprovedOperation();
        assertEquals("APPROVED", SQLHelper.getCreditPayment());
    }

    @Test
    @DisplayName("Запрещенный номер карты при оплате картой")
    // ошибка, банк одобряет операцию, хотя статус Declined
    public void shouldDeclinedCardPayment() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        debitCardPage.fillCardPaymentForm(declinedCardNumber, validMonth, validYear, validOwnerName, validCode);
        debitCardPage.bankDeclinedOperation();
        assertEquals("Declined", SQLHelper.getCreditPayment());
    }
    ///НОМЕР КАРТЫ
    @Test
    @DisplayName("Неполный номер карты при оплате картой")
    public void shouldHandleInvalidCard() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var invalidCardNumber = DataHelper.GetAShortNumber();
        debitCardPage.fillCardPaymentForm(invalidCardNumber, validMonth, validYear, validOwnerName, validCode);
        debitCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата картой с незаполненным полем \"Номер карты\"")
    public void shouldHandleEmptyCardNumber() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var emptyCardNumber = DataHelper.getEmptyField();
        debitCardPage.fillCardPaymentForm(emptyCardNumber, validMonth, validYear, validOwnerName, validCode);
        debitCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата картой с спец.символами в имени")
    public void shouldHandleSpecialSymbolsCardNumber() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var specSymbols = DataHelper.getSpecialSymbols();
        debitCardPage.fillCardPaymentForm(specSymbols, validMonth, validYear, validOwnerName, validCode);
        debitCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата картой с буквами в имени")
    // ошибка, неверное наименование ошибки. Должно быть "Поле обязательно для заполнения"
    public void shouldHandleLettersInNumber() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var specialLetters = DataHelper.getSpecialLetters();
        debitCardPage.fillCardPaymentForm(specialLetters, validMonth, validYear, validOwnerName, validCode);
        debitCardPage.emptyField();
    }
    @Test
    @DisplayName("Оплата картой с валидной картой отсутствующей в базе")
    public void shouldHandNumberNoBase() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var notBase = DataHelper.getCardNoBase();
        debitCardPage.fillCardPaymentForm(notBase, validMonth, validYear, validOwnerName, validCode);
        debitCardPage.bankDeclinedOperation();
    }
    ///МЕСЯЦ
    @Test
    @DisplayName("Оплата картой c истекшим месяцем")
    public void shouldHandleExpiredCardMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var monthExpired = DataHelper.getRandomMonth(-1);
        debitCardPage.fillCardPaymentForm(approvedCardNumber, monthExpired, validYear, validOwnerName, validCode);
        debitCardPage.errorCardTermValidity();
    }
    @Test
    @DisplayName("Оплата картой с 00 в месяце ")
    //ошибка, операция одобрена, должен быть неверный формат
    public void shouldHandleTwoDigitMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var oneDigital = DataHelper.getInvalidTwo();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, oneDigital, validYear, validOwnerName, validCode);
        debitCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата картой с 0 в месяце ")
    public void shouldHandleOneDigitMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var oneDigital = DataHelper.getInvalidOne();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, oneDigital, validYear, validOwnerName, validCode);
        debitCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата картой с спец.символами в месяце ")
    //спец символы не вводятся, поэтому ошибка должна быть "Поле обязательно для заполнения"
    public void shouldHandleSpecSymbolsMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var specSymbols = DataHelper.getSpecialSymbols();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, specSymbols, validYear, validOwnerName, validCode);
        debitCardPage.emptyField();
    }
    @Test
    @DisplayName("Оплата картой с пустым полем в месяце ")
    // ошибка должна быть "Поле обязательно для заполнения"
    public void shouldHandleEmptyFieldMonth() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var emptyField = DataHelper.getEmptyField();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, emptyField, validYear, validOwnerName, validCode);
        debitCardPage.emptyField();
    }

    ///ГОД
    @Test
    @DisplayName("Оплата картой c истекшим годом")
    public void shouldHandleExpiredCardYear() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var expiredYear = DataHelper.getRandomYear(-5);
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, expiredYear, validOwnerName, validCode);
        debitCardPage.termValidityExpired();
    }

    @Test
    @DisplayName("Оплата картой с 0 в годе ")
    public void shouldHandleOneDigitYear() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var oneDigital = DataHelper.getInvalidOne();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, oneDigital, validOwnerName, validCode);
        debitCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата картой с спец.символами в годе ")
    //спец символы не вводятся, поэтому ошибка должна быть "Поле обязательно для заполнения"
    public void shouldHandleSpecSymbolsYear() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var specSymbols = DataHelper.getSpecialSymbols();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, specSymbols, validOwnerName, validCode);
        debitCardPage.emptyField();
    }
    @Test
    @DisplayName("Оплата картой с пустым полем в годе ")
    // ошибка должна быть "Поле обязательно для заполнения"
    public void shouldHandleEmptyFieldYear() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var emptyField = DataHelper.getEmptyField();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, emptyField, validOwnerName, validCode);
        debitCardPage.emptyField();
    }
    ///ВЛАДЕЛЕЦ
    @Test
    @DisplayName("Оплата картой c именем на кириллице")
    //ошибка, оплата одобрена, должен быть неверный формат
    public void shouldHandleInvalidOwnerNameRu() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();;
        var rusLanguageName = DataHelper.getRandomNameRus();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, rusLanguageName, validCode);
        debitCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата картой c цифрами в имени")
    //ошибка, операция одобрена, должен быть неверный формат
    public void shouldHandleInvalidOwnerNameDigits() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var digitsName = DataHelper.getNumberName();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, digitsName, validCode);
        debitCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата картой c спец.символами в имени")
    //ошибка, операция одобрена, должен быть неверный формат
    public void shouldHandleInvalidOwnerNameSpecialSymbols() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var specSymbolsName = DataHelper.getSpecialSymbols();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, specSymbolsName, validCode);
        debitCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата картой пустым полем имени")
    public void shouldHandleEmptyName() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var emptyName = DataHelper.getEmptyField();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, emptyName, validCode);
        debitCardPage.emptyField();
    }
    ///CVC
    @Test
    @DisplayName("Оплата картой c 2 цифрами в  CVC ")
    public void shouldHandleTwoDigitCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var twoDigitCVC = DataHelper.getNumberCVC(2);
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, twoDigitCVC);
        debitCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата картой c 1 цифрой в CVC ")
    public void shouldHandleOneDigitCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var oneDigitCVC = DataHelper.getNumberCVC(1);
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, oneDigitCVC);
        debitCardPage.errorFormat();
    }
    @Test
    @DisplayName("Оплата картой c 4 цифрами в CVC ")
    public void shouldHandleFourDigitCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var oneDigitCVC = DataHelper.getNumberCVC(4);
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, oneDigitCVC);
        debitCardPage.bankApprovedOperation();
    }

    @Test
    @DisplayName("Оплата картой c пустым поле CVC ")
    //ошибка, в поле Владелец также отображается ошибка "Поле обязательно для заполнения"
    public void shouldHandleEmptyCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var emptyCVC = DataHelper.getEmptyField();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, emptyCVC);
        debitCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата картой c спец.символами CVC ")
    //ошибка, в поле Владелец отображается ошибка "Поле обязательно для заполнения"
    public void shouldHandleSpecialSymbolsInCvc() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var specSymbolsCVC = DataHelper.getSpecialSymbols();
        debitCardPage.fillCardPaymentForm(approvedCardNumber, validMonth, validYear, validOwnerName, specSymbolsCVC);
        debitCardPage.errorFormat();
    }

    @Test
    @DisplayName("Оплата картой все поля пустые")
    public void shouldHandleAllFieldsEmpty() {
        PaymentTypesPage page = new PaymentTypesPage();
        page.paymentTypesPage();
        var debitCardPage = page.cardPayment();
        var emptyField = DataHelper.getEmptyField();
        debitCardPage.fillCardPaymentForm(emptyField, emptyField, emptyField, emptyField, emptyField);
        debitCardPage.errorFormat();
    }
}