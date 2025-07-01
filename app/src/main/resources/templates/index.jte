@import hexlet.code.model.dto.BasePage
@param BasePage page

<html lang="en">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
<!--    <title>Hello World</title>-->
    <title>Добавление сайта</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
            crossorigin="anonymous"></script>
</head>

<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card p-4 shadow-sm">
                <h2 class="mb-4 text-center">Проверка ссылки</h2>
                <form action="/urls" method="post" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="url-input" class="form-label">Ссылка</label>
                        <input type="text" class="form-control" id="url-input" name="url" aria-label="url" required>
                        @if(page != null && page.getFlash() != null)
                            <p>${page.getFlash()}</p>
                        @endif
                        <div class="invalid-feedback">
                            Пожалуйста, введите ссылку.
                        </div>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">Проверить</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="card-body p-0 d-flex justify-content-center">
        <a class="mt-5 px-2" href="/urls">Список сайтов</a>
    </div>
</div>
</body>
</html>