<?php
    require_once "./code/HtmlOut.php";
    $htmlOut = new HtmlOut();
?>
<!DOCTYPE html>
<html lang="zh">
<head>
    <?php $htmlOut->setHead(); ?>
</head>
<body>
    <div class="container">
        <?php $htmlOut->show_homePage(); ?>
    </div>
</body>
</html>
