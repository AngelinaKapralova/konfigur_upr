## Этап 2. Конфигурация

### Общее описание

На втором этапе эмулятор стал настраиваемым:
- параметры командной строки `--vfs` (путь к VFS) и `--script`
  (путь к стартовому скрипту);
- при запуске заданные параметры выводятся в окно и в консоль
  со строками `[debug]`;
- стартовый скрипт выполняется автоматически: в окне видно и ввод,
  и вывод, как будто команды вводит пользователь;
- скрипт останавливается на первой ошибке и сообщает номер строки;
- служебная команда `conf-dump` выводит параметры в виде `ключ=значение`;
- созданы скрипты ОС для проверки всех параметров.

### Описание классов и методов

- `Config` хранит параметры запуска. Метод `dump` выводит их строками
  `ключ=значение`.
- `ArgsParser` методом `parse` разбирает массив аргументов `main`
  и создаёт `Config`. Неизвестная опция или пропущенное значение
  приводят к ошибке.
- `ScriptRunner` методом `run` читает файл скрипта построчно и выполняет
  каждую строку. Пустые строки пропускаются, строки с `#` показываются,
  но не выполняются.
- `Main` теперь разбирает параметры и передаёт их в окно.
- `ShellWindow` печатает `[debug]` и запускает стартовый скрипт.
- `Shell` получил команду `conf-dump`.

### Описание настроек

| Параметр | Описание |
|---|---|
| `--vfs <путь>` | путь к физическому расположению VFS |
| `--script <путь>` | путь к стартовому скрипту |

Оба параметра необязательны. Если параметр не задан, выводится `<not set>`.

### Описание команд для сборки и запуска тестов

    make run ARGS="--vfs vfs/minimal --script scripts/startup_ok.txt"

Скрипты для проверки параметров:

    bash scripts/test_no_params.sh       без параметров
    bash scripts/test_vfs_param.sh       только --vfs
    bash scripts/test_script_param.sh    только --script
    bash scripts/test_all_params.sh      оба параметра
    bash scripts/test_script_error.sh    скрипт с ошибкой
    bash scripts/test_script_missing.sh  файл скрипта не найден
    bash scripts/test_bad_args.sh        неверные параметры

### Примеры использования

Запуск без параметров:

    [debug] startup parameters:
    [debug]   vfs-path=<not set>
    [debug]   script-path=<not set>

Запуск с обоими параметрами:

    [debug] startup parameters:
    [debug]   vfs-path=vfs/minimal
    [debug]   script-path=scripts/startup_ok.txt
    vfs$ # Стартовый скрипт: проверка команд этапа 2
    vfs$ conf-dump
    vfs-path=vfs/minimal
    script-path=scripts/startup_ok.txt

Скрипт с ошибкой:

    vfs$ conf-dump
    vfs-path=<not set>
    script-path=scripts/startup_error.txt
    vfs$ cd a b
    cd: too many arguments
    script error: stopped at line 3

Неверный параметр (окно не открывается):

    error: unknown option: --unknown
    usage: java -cp out shell.Main [--vfs <path>] [--script <path>]