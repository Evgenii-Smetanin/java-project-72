@import hexlet.code.model.dto.UrlPage
@param UrlPage page

<html lang="en">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Hello World</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
            crossorigin="anonymous"></script>
</head>

<body>
<section class="py-4">
    <div class="container-lg">
        <h1 class="mb-4">Сайты</h1>
        @if(page != null && page.getFlash() != null)
        <p>${page.getFlash()}</p>
        @endif
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>Список сайтов</span>
            </div>
            <div class="card-body p-0">
                <table class="table table-striped table-bordered table-hover mb-0">
                    <thead class="table-light">
                    <tr>
                        <th style="width: 5%;">ID</th>
                        <th style="width: 5%;">Имя</th>
                        <th style="width: 20%;">Последняя проверка</th>
                        <th style="width: 10%;">Код ответа</th>
                    </tr>
                    </thead>

                    <tbody>
                    <tr>
                        <td>${page.getUrl().getId()}</td>
                        <td>${page.getUrl().getName()}</td>
                        <td>${page.getUrl().getCreatedAt().toString()}</td>
                        <td></td>
                    </tr>
                    </tbody>
                </table>

                <h2 class="mt-5">Проверки</h2>

                <form method="post" action="/urls/1/checks">
                    <button type="submit" class="btn btn-primary">Проверить</button>
                </form>

                <table class="table table-striped table-bordered table-hover mb-0">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Статус</th>
                        <th>Заголовок вкладки</th>
                        <th>Заголовок первого уровня</th>
                        <th>Описание</th>
                        <th>Дата проверки</th>
                    </tr>
                    </thead>

                    <tbody>
                    @for(var check : page.getUrl().getChecks())
                    <tr>
                        <td>${check.getId()}</td>
                        <td>${check.getStatusCode()}</td>
                        <td>${check.getTitle()}</td>
                        <td>${check.getH1()}</td>
                        <td>${check.getDescription()}</td>
                        <td>${check.getCreatedAt().toString()}</td>
                        <td></td>
                    </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>
</body>

</html>