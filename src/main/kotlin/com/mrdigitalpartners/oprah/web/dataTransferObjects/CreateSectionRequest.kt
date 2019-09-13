package com.mrdigitalpartners.oprah.web.dataTransferObjects

import com.mrdigitalpartners.oprah.core.TemplateQuestion

data class CreateSectionRequest(val title: String, val questions: List<TemplateQuestion>)
