package kitchenpos;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("메뉴 기능")
public class MenuAcceptanceTest extends ApplicationTest {

    private final Menu 기본_메뉴 = new Menu();
    private List<MenuProduct> 메뉴_구성_상품;

    @BeforeEach
    public void setUp() {
        super.setUp();
        MenuGroupAcceptanceTest.메뉴_그룹_생성(기본_메뉴_그룹);
        메뉴_구성_상품 = getMenuProducts(1L, 3);
        기본_메뉴.setName("후라이드+후라이드");
        기본_메뉴.setPrice(BigDecimal.valueOf(15000));
        기본_메뉴.setMenuGroupId(1L);
        기본_메뉴.setMenuProducts(메뉴_구성_상품);
    }

    @DisplayName("메뉴 등록에 성공하면 201 응답을 받는다.")
    @Test
    void createMenuGroup() {
        //when
        ExtractableResponse<Response> response = 메뉴_추가(기본_메뉴);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("전체 메뉴를 불러오는데 성공하면, 200 응답을 받는다.")
    @Test
    void getMenuGroup() {

        //given
        ExtractableResponse<Response> response = 전체_메뉴_조회();

        //when
        List<Menu> menus = response.jsonPath().getList(".", Menu.class);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(menus).hasSize(6);  // defaultData 6
    }

    private List<MenuProduct> getMenuProducts(final Long productId, final long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);

        return Collections.singletonList(menuProduct);
    }

    private ExtractableResponse<Response> 메뉴_추가(final Menu menu) {
        return RestAssured
            .given().log().all()
            .body(menu)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post("/api/menus")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> 전체_메뉴_조회() {
        return RestAssured
            .given().log().all()
            .when().get("/api/menus")
            .then().log().all()
            .extract();
    }
}
