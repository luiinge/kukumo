<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta content="IE=edge" http-equiv="X-UA-Compatible"/>
    <meta content="width=device-width, initial-scale=1" name="viewport">

    <script src="https://cdn.jsdelivr.net/npm/chart.js@3.5.1/dist/chart.min.js" type="text/javascript"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.7.0/highlight.min.js"></script>
    <script>${globalScript}</script>

    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Outlined" rel="stylesheet"/>
    <style>${globalStyle}</style>

    <title>Report</title>

</head>
<body>
<div id="report">

    <div class="navbar">
        <div class="report-info">
            <button class="menu-button toggle" title="Menu" type="button"><span></span></button>
            <h1 class="report-title" title="${title!plan.name!plan.displayName}">${title!plan.name!plan.displayName}</h1>
        </div>
        <span id="logo"></span>
    </div>

    <div class="details container" id="details">

        <ul class="test--list details--list">
            <li class="details--item">
                <section class="test--component test--no-tests">
                    <header class="test--header">
                        <button class="test--header-btn" type="button">
                            <h3 class="test--title">Feature Results</h3>
                            <hr/>
                        </button>
                    </header>
                    <div class="test--body details--body">
                        <canvas class="chart" data-result='{<#if plan.childrenResults??><#list plan.childrenResults as k, v>"${k}":${v}<#sep>, </#list></#if>}'>
                        </canvas>
                    </div>
                </section>
            </li>
            <li class="details--item">
                <section class="test--component test--no-tests">
                    <header class="test--header">
                        <button class="test--header-btn" type="button">
                            <h3 class="test--title">Scenario Results</h3>
                            <hr/>
                        </button>
                    </header>
                    <div class="test--body details--body">
                        <canvas class="chart" data-result='{<#list plan.testCaseResults as k, v>"${k}":${v}<#sep>,</#list>}'>
                        </canvas>
                    </div>
                </section>
            </li>
            <li class="details--item">
                <section class="test--component test--no-tests">
                    <header class="test--header">
                        <button class="test--header-btn" type="button">
                            <h3 class="test--title">Run info</h3>
                            <hr/>
                        </button>
                    </header>
                    <div class="test--body details--body">
                        <ul>
                            <#if project??>
                                <li>
                                    <label>Project name</label>
                                    <span>${project}</span>
                                </li>
                            </#if>
                            <li>
                                <label>Wakamiti version</label>
                                <span>${version!"?.?.?"}</span>
                            </li>
                            <li>
                                <label>Execution start</label>
                                <span>${plan.startInstant?datetime.xs?string.medium}</span>
                            </li>
                            <li>
                                <label>Execution end</label>
                                <span>${plan.finishInstant?datetime.xs?string.medium}</span>
                            </li>
                            <li>
                                <label>Total duration</label>
                                <span>${plan.duration?string.@duration}</span>
                            </li>
                        </ul>
                    </div>
                </section>
            </li>
        </ul>


        <ul class="suite--list">

            <#if plan.children??>
                <#list plan.children as feature>
            <li id="${feature.id?replace("#", "")}">
                <section class="suite--component">
                    <header class="suite--header">
                        <button class="test--header-btn toggle toggle-group <#if feature.result != "PASSED">on</#if>" type="button">
                            <h2 class="suite--title">
                                <span><strong class="keyword">${feature.keyword}:</strong> ${feature.name!""}</span>
                                <i class="material-icons md-18">expand_more</i>
                            </h2>
                            <#if feature.description??>
                                <#list feature.description as line>
                                    <h6 class="test--comment">${line}</h6>
                                </#list>
                            </#if>
                            <ul class="test-summary--component">
                                <li class="test-summary--item" title="Duration">
                                    <i class="material-icons md-18">access_time</i>
                                    <span>${feature.duration?string.@duration}</span>
                                </li>
                                <li class="test-summary--item" title="Tests">
                                    <i class="material-icons md-18">article</i>
                                    <span><#if feature.childrenResults??>${sum(feature.childrenResults)}<#else>0</#if></span>
                                </li>
                                <#if feature.childrenResults??>
                                <li class="test-summary--item">
                                    <ul class="test-summary--results">
                                    <#list feature.childrenResults as resultType, count>
                                        <li class="test-summary--item" title="${resultType?capitalize}">
                                            <i class="material-icons md-18 icon--${resultType?lower_case}"></i>
                                            <span>${count}</span>
                                        </li>
                                    </#list>
                                    </ul>
                                </li>
                                </#if>
                            </ul>
                        </button>
                    </header>
                    <#if feature.children??>
                        <div class="suite--body collapsable">
                            <ul class="test--list">

                                <#macro stepPanel node>
                                    <li class="step--component">
                                        <header class="step--header">
                                            <button class="step--header-btn toggle <#if node.result != "PASSED">on</#if>" type="button">
                                                <i class="material-icons md-18 icon--${node.result?lower_case}"></i>
                                                <h5 class="step--title">
                                                    <span><strong class="keyword">${node.keyword}</strong> ${node.name!""}</span>
                                                </h5>
                                                <div class="step--info">
                                                    <#if node.errorMessage??>
                                                        <i class="material-icons-outlined md-18" title="<#outputformat "HTML">${node.errorMessage}</#outputformat>">feedback</i>
                                                    </#if>
                                                    <span>${node.duration?string.@duration}</span>
                                                    <i class="material-icons md-18 step--duration-icon">access_time</i>
                                                </div>
                                            </button>
                                        </header>
                                        <#if node.document?? || node.dataTable?? || node.errorTrace??>
                                            <div class="step--body-wrap collapsable">
                                                <#if node.document??>
                                                    <div class="step--body">
                                                        <pre class="step--code-snippet ${node.documentType!""} hljs"><code>${node.document}</code></pre>
                                                    </div>
                                                </#if>
                                                <#if node.dataTable??>
                                                    <div class="step--body ">
                                                        <table class="panel-body-datatable step--code-snippet hljs">
                                                            <thead>
                                                            <tr>
                                                                <#list node.dataTable[0] as cell><td>${cell}</td></#list>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <#list node.dataTable as row>
                                                                <#if row?counter == 1><#continue></#if>
                                                                <tr>
                                                                    <#list row as cell><td>${cell}</td></#list>
                                                                </tr>
                                                            </#list>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </#if>
                                                <#if node.errorTrace??>
                                                    <div class="step--body">
                                                        <pre class="step--code-snippet text hljs"><code class="step--${node.result?lower_case}-message"><#outputformat "HTML">${node.errorTrace}</#outputformat></code></pre>
                                                    </div>
                                                </#if>
                                            </div>
                                        </#if>
                                    </li>
                                </#macro>

                                <#macro scenarioPanel scenario>
                                    <li id="${scenario.id?replace("#", "")}">
                                        <section class="test--component">
                                            <header class="test--header">
                                                <button class="test--header-btn toggle toggle-group <#if scenario.result != "PASSED">on</#if>" type="button">
                                                    <#if scenario.tags??>
                                                        <h5 class="test--title">
                                                            <span class="test--tags"><#list scenario.tags as tag>@${tag} </#list></span>
                                                            <i class="material-icons md-18">expand_more</i>
                                                        </h5>
                                                    </#if>
                                                    <h3 class="test--title">
                                                        <span><strong class="keyword">${scenario.keyword}:</strong> ${scenario.name!""}</span>
                                                        <#if scenario.tags??><#else>
                                                            <i class="material-icons md-18">expand_more</i>
                                                        </#if>
                                                    </h3>
                                                    <#if scenario.description??>
                                                        <#list scenario.description as line>
                                                            <h6 class="test--comment">${line}</h6>
                                                        </#list>
                                                    </#if>
                                                    <ul class="test-summary--component">
                                                        <li class="test-summary--item" title="Duration">
                                                            <i class="material-icons md-18">access_time</i>
                                                            <span>${scenario.duration?string.@duration}</span>
                                                        </li>
                                                        <li class="test-summary--item" title="Tests">
                                                            <i class="material-icons md-18">article</i>
                                                            <span><#if scenario.childrenResults??>${sum(scenario.childrenResults)}<#else>0</#if></span>
                                                        </li>
                                                        <#if scenario.childrenResults??>
                                                            <li class="test-summary--item">
                                                                <ul class="test-summary--results">
                                                                    <#list scenario.childrenResults as resultType, count>
                                                                        <li class="test-summary--item" title="${resultType?capitalize}">
                                                                            <i class="material-icons md-18 icon--${resultType?lower_case}"></i>
                                                                            <span>${count}</span>
                                                                        </li>
                                                                    </#list>
                                                                </ul>
                                                            </li>
                                                        </#if>
                                                    </ul>
                                                </button>
                                            </header>
                                            <#if scenario.children??>
                                                <div class="test--body collapsable">
                                                    <ul class="step--list">
                                                        <#list scenario.children as child>
                                                            <#if child.children??>
                                                                <li>
                                                                    <section class="test--component">
                                                                        <header class="test--header">
                                                                            <button class="test--header-btn toggle toggle-group <#if child.result != "PASSED">on</#if>" type="button">
                                                                                <h5 class="test--title">
                                                                                    <span><strong class="keyword">${child.keyword}:</strong> ${child.name!""}</span>
                                                                                    <i class="material-icons md-18">expand_more</i>
                                                                                </h5>
                                                                            </button>
                                                                        </header>
                                                                        <div class="test--body collapsable">
                                                                            <ul class="step--list">
                                                                                <#list child.children as child2>
                                                                                    <@stepPanel child2 />
                                                                                </#list>
                                                                            </ul>
                                                                        </div>
                                                                    </section>
                                                                </li>
                                                            <#else>
                                                                <@stepPanel child />
                                                            </#if>
                                                        </#list>

                                                    </ul>
                                                </div>
                                            </#if>
                                        </section>
                                    </li>
                                </#macro>

                                <#list feature.children as scenario>
                                    <#if scenario.nodeType == "AGGREGATOR" && scenario.children??>
                                        <#list scenario.children as child>
                                            <@scenarioPanel child />
                                        </#list>
                                    <#else>
                                        <@scenarioPanel scenario />
                                    </#if>
                                </#list>
                            </ul>
                        </div>
                    </#if>
                </section>
            </li>
                </#list>
            </#if>
        </ul>
    </div>

    <footer>
        <div class="container">
            <p>©2023 Made with <span class="heart">♥</span> by <a href="https://www.iti.es/" rel="noopener noreferrer"
                                                                  target="_blank">ITI</a>
                • <a href="https://github.com/iti-ict/wakamiti/tree/main/kukumo-plugins/kukumo-html-report"
                     rel="noopener noreferrer" target="_blank">wakamiti-html-report</a> v1.6.0</p>
        </div>
    </footer>


    <nav class="nav-menu--menu collapsable">
        <#assign results = plan.testCaseResults?keys?map(k -> k.toString())>
        <div class="nav-menu--section">
            <div class="nav-menu--control<#if results?seq_contains("PASSED")><#else> toggle-switch--disabled</#if>">
                <i class="material-icons icon--passed"></i>
                <label for="passed-toggle">Show Passed
                    <input id="passed-toggle" type="checkbox" <#if results?seq_contains("PASSED")>checked<#else>disabled</#if>>
                    <span class="toggle-switch--toggle"></span>
                </label>
            </div>
            <div class="nav-menu--control<#if results?seq_contains("NOT_IMPLEMENTED")><#else> toggle-switch--disabled</#if>">
                <i class="material-icons icon--not-implemented"></i>
                <label for="not-implemented-toggle">Show Not Implemented
                    <input id="not-implemented-toggle" type="checkbox" <#if results?seq_contains("NOT_IMPLEMENTED")>checked<#else>disabled</#if>>
                    <span class="toggle-switch--toggle"></span>
                </label>
            </div>
            <div class="nav-menu--control<#if results?seq_contains("SKIPPED")><#else> toggle-switch--disabled</#if>">
                <i class="material-icons icon--skipped"></i>
                <label for="skipped-toggle">Show Skipped
                    <input id="skipped-toggle" type="checkbox" <#if results?seq_contains("SKIPPED")>checked<#else>disabled</#if>>
                    <span class="toggle-switch--toggle"></span>
                </label>
            </div>
            <div class="nav-menu--control<#if results?seq_contains("UNDEFINED")><#else> toggle-switch--disabled</#if>">
                <i class="material-icons icon--undefined"></i>
                <label for="undefined-toggle">Show Undefined
                    <input id="undefined-toggle" type="checkbox" <#if results?seq_contains("UNDEFINED")>checked<#else>disabled</#if>>
                    <span class="toggle-switch--toggle"></span>
                </label>
            </div>
            <div class="nav-menu--control<#if results?seq_contains("FAILED")><#else> toggle-switch--disabled</#if>">
                <i class="material-icons icon--failed"></i>
                <label for="failed-toggle">Show Failed
                    <input id="failed-toggle" type="checkbox" <#if results?seq_contains("FAILED")>checked<#else>disabled</#if>>
                    <span class="toggle-switch--toggle"></span>
                </label>
            </div>
            <div class="nav-menu--control<#if results?seq_contains("ERROR")><#else> toggle-switch--disabled</#if>">
                <i class="material-icons icon--error"></i>
                <label for="error-toggle">Show Failed
                    <input id="error-toggle" type="checkbox" <#if results?seq_contains("ERROR")>checked<#else>disabled</#if>>
                    <span class="toggle-switch--toggle"></span>
                </label>
            </div>
        </div>

        <div class="nav-menu--section">
            <ul>
                <#list plan.children as feature>
                    <li>
                        <a href="#${feature.id?replace("#", "")}">
                            <span class="list-style icon--${feature.result?lower_case}"></span>
                            <span>${feature.name!feature.displayName!""}</span>
                        </a>
                        <div>
                            <ul class="nav-menu--sub">
                                <#macro scenarioMenu scenario>
                                    <li data-target="${scenario.result?lower_case}-toggle">
                                        <a href="#${scenario.id?replace("#", "")}">
                                            <span class="list-style icon--${scenario.result?lower_case}"></span>
                                            <span>${scenario.name!scenario.displayName!""}</span>
                                        </a>
                                    </li>
                                </#macro>
                                <#list feature.children as scenario>
                                    <#if scenario.nodeType == "AGGREGATOR" && scenario.children??>
                                        <#list scenario.children as child>
                                            <@scenarioMenu child />
                                        </#list>
                                    <#else>
                                        <@scenarioMenu scenario />
                                    </#if>
                                </#list>
                            </ul>
                        </div>
                    </li>
                </#list>
            </ul>

        </div>
    </nav>
</div>
</body>
</html>