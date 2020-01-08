import com.andreamazzarella.amaze.utils.Err
import com.andreamazzarella.amaze.utils.Ok
import com.andreamazzarella.amaze.utils.Result
import org.junit.jupiter.api.Assertions

fun <O, E> assertOkEquals(expected: O, result: Result<O, E>) {
    when (result) {
        is Ok -> Assertions.assertEquals(result.okValue, expected)
        is Err -> Assertions.fail("Expected result to be ok, but was an error: ${result.errorValue}")
    }
}

fun <O, E> assertOk(result: Result<O, E>, assertion: (O) -> Unit) {
    when (result) {
        is Ok -> assertion(result.okValue)
        is Err -> Assertions.fail("Expected result to be ok, but was an error: ${result.errorValue}")
    }
}

fun <O, E> assertError(result: Result<O, E>, assertion: (E) -> Unit) {
    when (result) {
        is Ok -> Assertions.fail("Expected result to be an error, but was ok")
        is Err -> assertion(result.errorValue)
    }
}

fun <O, E> assertIsError(expected: E, result: Result<O, E>) {
    when (result) {
        is Ok -> Assertions.fail("Expected result to be an error, but was ok")
        is Err -> Assertions.assertEquals(expected, result.errorValue)
    }
}

fun <O, E> assertIsOk(result: Result<O, E>) {
    when (result) {
        is Ok -> Assertions.assertEquals("pass", "pass")
        is Err -> Assertions.fail("Expected result to be ok, but was an error")
    }
}
