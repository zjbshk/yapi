def dealHTML(exportStr, doc) {
    String scriptHtml = '''
            <link rel="stylesheet" type="text/css" href="js/layui/dist/css/layui.css"/>
            <script src="js/ScrollMagic.min.js" type="text/javascript"></script>
            <script src="js/clipboard.min.js" type="text/javascript"></script>
            <script src="js/layui/dist/layui.js" type="text/javascript" charset="utf-8"></script>
            <script src="js/index.js" type="text/javascript" charset="utf-8"></script>
            <link rel="stylesheet" href="css/default.css"/>
            <script src="js/highlight.min.js"></script>
            <script>hljs.initHighlightingOnLoad();</script>
            <link rel="stylesheet" href="css/index.css">'''

    titleEle = doc.getElementsByTag("title").first()
    titleEle.after(scriptHtml)

    return doc.toString()
}