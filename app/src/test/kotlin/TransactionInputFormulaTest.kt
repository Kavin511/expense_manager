import com.devstudio.utils.formulas.TransactionInputFormula
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TransactionInputFormula {
    @Test
    fun `anything divide by zero returns 0`() {
        val result = TransactionInputFormula().calculate("1/0")
        assertThat(result).isEqualTo(0.0)
    }

    @Test
    fun `any operator at end should not be considered`() {
        assertThat(TransactionInputFormula().calculate("1/4+11/")).isEqualTo(11.25)
        assertThat(TransactionInputFormula().calculate("1x5-60+")).isEqualTo(-55.0)
        assertThat(TransactionInputFormula().calculate("-1")).isEqualTo(-1.00)
        assertThat(TransactionInputFormula().calculate("14+23+12/3X")).isEqualTo(16.33)
    }

    @Test
    fun `test decimal operations`(){
        assertThat(TransactionInputFormula().calculate("1.4+2.3+12/3")).isEqualTo(5.23)
        assertThat(TransactionInputFormula().calculate(".14+2.3+12/3")).isEqualTo(4.81)
    }

    @Test
    fun `test for negative values`(){
        assertThat(TransactionInputFormula().calculate("-.14+2.3+12/3")).isEqualTo(4.72)
    }

    @Test
    fun `0 divide by anything returns 0`() {
        val result = TransactionInputFormula().calculate("0/1")
        assertThat(result).isEqualTo(0.0)
    }

    @Test
    fun `sum of multiple numbers`() {
        val result = TransactionInputFormula().calculate("1+2+4+1+4")
        assertThat(result).isEqualTo(12.0)
    }
}