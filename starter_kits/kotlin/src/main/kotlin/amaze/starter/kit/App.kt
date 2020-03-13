package amaze.starter.kit

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

val SERVER_URL: String = TODO("replace this placeholder with the server url (e.g. https://myserver.com)")

fun main() {
    when (val result = postQuery()) {
        is Result.Success -> println("Success!! - ${result.value.data.sampleQuery.aString}")
        is Result.Failure -> println("Your request failed")
    }
}

data class GraphQLQuery(val query: String)
val query = """
        query {
            sampleQuery {
              aString
            }
        }
    """.trimIndent()


data class GraphQLResponse(var data: GraphQLData) {
    data class GraphQLData(var sampleQuery: SampleQuery) {
        data class SampleQuery(var aString: String)
    }
}

private fun postQuery(): Result<GraphQLResponse, FuelError> {
    val (_, _, result) =
        "$SERVER_URL/graphql"
            .httpPost()
            .jsonBody(GraphQLQuery(query))
            .responseObject<GraphQLResponse>()

    return result
}

