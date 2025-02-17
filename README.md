# Дипломный проект профессии «Тестировщик»

## Документы
* [План автоматизации](https://github.com/captainchaan/Diplom/blob/main/Plan.md)
* [Отчет по итогам тестирования](https://github.com/captainchaan/Diplom/blob/main/docs/Report.md)
* [Отчет по итогам автоматизации](https://github.com/captainchaan/Diplom/blob/main/docs/Summary.md)

Дипломный проект представляет собой автоматизацию тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка.

## Описание приложения

Приложение представляет из себя веб-сервис "Путешествие дня".

Приложение предлагает купить тур по определённой цене с помощью двух способов:
1. Обычная оплата по дебетовой карте
2. Уникальная технология: выдача кредита по данным банковской карты

Само приложение не обрабатывает данные по картам, а пересылает их банковским сервисам:
* сервису платежей (далее - Payment Gate)
* кредитному сервису (далее - Credit Gate)

Приложение должно в собственной СУБД сохранять информацию о том, каким способом был совершён платёж и успешно ли он был совершён (при этом данные карт сохранять не допускается).

## Запуск тестов

* склонировать репозиторий `git clone`
* для запуска контейнеров с MySql, PostgreSQL и Node.js использовать команду `docker-compose up -d --build` (необходим установленный Docker); чтобы образ не пересобирался каждый раз необходимо убрать флаг --build
* запуск приложения:
    * для запуска под MySQL использовать команду 
    ```
    java -"Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar
    ```
    * для запуска под PostgreSQL использовать команду 
    ```
    java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar
    ```
* запуск тестов (Allure):
   * для запуска под MySQL использовать команду 
   ```
   gradlew  clean test
   ```
   * для запуска под PostgreSQL использовать команду 
   ```
   gradlew -Ddb.url=jdbc:postgresql://localhost:5432/app clean test
   ```
    *По умолчанию тесты запускаются для "http://localhost:8080/", чтобы изменить адрес, необходимо дополнительно указать `-Dsut.url=...`  
    *Чтобы использовать для подключения к БД логин и пароль отличные от указанных по умолчанию, необходимо дополнительно указать `-Ddb.user=...` и `-Ddb.password=...`
* для получения отчета (Allure) использовать команду `gradlew allureServe`
* после окончания тестов завершить работу приложения (Ctrl+C), остановить контейнеры командой `docker-compose down`

