### Структура проекта 
Проект состоит из двух частей - скрипт обновления   
(есть легаси версия update.py и новая baloons-kotlin которая работает через cds)   
и часть с сайтом - ball.py 
### Запуск скрипта обновления
1. Скачайте данный репозиторий
2. Запустите скрипт ball.py для создания структуры mariadb  
3. Создайте файл secrets.properties с полями gitUser и gitPassword(получить данные для них можно [здесь](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages))
4. Запустите ```./gradlew run --args='-c config'  ```, где config это папка с конфигом парсера.  
Формат конфига можно посмотреть [тут](https://github.com/icpc/live-v3).
Логин и пароль бд указывается как переменные окружения(USER и PASSWORD соответственно)
### Запуск сайта
1. Скачайте [репозиторий](https://github.com/Nikkirche/balloons)
2. Сгенируйте сертификат openssl req -x509 -newkey rsa:4096 -nodes -out cert.pem -keyout key.pem -days 365
3. Добавьте в config.py ключи от vk(не проверялось что работает)/google(Ключи можно получить вот [здесь](https://console.cloud.google.com)) и кусок ```db': { 'host': <host>, 'user': <user>, 'db': <db-name>, 'passwd': <passwd> }```
4. Запустите через python ball.py
5. Добавтье external_id своего пользователя в список allowed_users в файле config.py