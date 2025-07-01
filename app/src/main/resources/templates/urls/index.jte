@import hexlet.code.model.dto.UrlsPage
@import java.time.format.DateTimeFormatterBuilder
@import java.time.format.DateTimeFormatter
@param UrlsPage page

<html lang="en">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Список сайтов</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
            crossorigin="anonymous"></script>
</head>

<body>
<section class="py-4">
    <div class="container-lg">
<!--        <h1 class="mb-4">Сайты</h1>-->
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
                    @for(var url : page.getUrls())
                    <tr>
                        <td>${url.getId()}</td>
                        <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                        <td>${url.getLastCheck() == null ? "" : url.getLastCheck().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))}</td>
                        <td>${url.getLastStatusCode() == 0 ? "" : String.valueOf(url.getLastStatusCode())}</td>
                    </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        </div>
        <div class="card-body p-0 d-flex justify-content-center">
            <a class="mt-5 px-2" href="/">Добавление сайта</a>
        </div>
    </div>
</section>
</body>

</html>