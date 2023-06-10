package io.ggamnyang.bt.utils

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*

internal class ApiDocumentUtils {

    companion object {
        val documentRequest: OperationRequestPreprocessor =
            preprocessRequest(
                modifyUris() // 문서상 URI를 변경
                    .scheme("https")
                    .host("docs.api.com")
                    .removePort(),
                prettyPrint()
            ) // Print request pretty
        val documentResponse: OperationResponsePreprocessor =
            preprocessResponse(prettyPrint()) // Print response pretty
    }
}
