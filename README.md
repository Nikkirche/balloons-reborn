### Структура проекта 
Проект состоит из двух частей - скрипт обновления   
(есть легаси версия update.py и новая baloons-kotlin которая работает через cds)   
и часть с сайтом - ball.sql
### Пререквизиты
* java 21
* mariadb 
* python 3
### Запуск скрипта обновления(CDS)
1. Скачайте данный репозиторий
2. Запустите ```./gradlew run --args='-c config'  ```, где config это папка с конфигом парсера.  
Формат конфига можно посмотреть [тут](https://github.com/icpc/live-v3).
Логин и пароль бд указывается в папке с конфигом в файле db.json. Для примера можно посмотреть на config-sample
### Запуск сайта
1. Скачайте [репозиторий](https://github.com/Nikkirche/balloons) из ветки 2024
2. Установите пакеты из requirements.txt 
3. Сгенируйте сертификат openssl req -x509 -newkey rsa:4096 -nodes -out cert.pem -keyout key.pem -days 365
4. Добавьте в config.py ключи от vk(не проверялось что работает)/google(Ключи можно получить вот [здесь](https://console.cloud.google.com)) и кусок ```db': { 'host': <host>, 'user': <user>, 'db': <db-name>, 'passwd': <passwd> }```
5. Запустите через python ball.py
6. Добавьте external_id своего пользователя в список allowed_users в файле config.py