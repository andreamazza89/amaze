package com.mrdigitalpartners.oprah.web.dataTransferObjects

data class SectionUpdateRequest(val title: String, val questionUpdates: List<QuestionUpdateRequest>)