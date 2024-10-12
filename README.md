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
'user': <user>, 'db': <db-name>, 'passwd': <passwd> }```
3. Запустите через python ball.py
4. Добавьте login своего пользователя в список allowed_users в файле config.py
