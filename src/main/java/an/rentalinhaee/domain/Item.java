package an.rentalinhaee.domain;

import an.rentalinhaee.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "cate")
@Getter @Setter
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;
    // 현재 재고
    private int stockQuantity;

    // 총 재고
    private int allStockQuantity;

    private int rentalCount;

    private String category;

    @OneToMany(mappedBy = "item")
    private List<Rental> rentals = new ArrayList<>();


    /**
     * 대여 가능 재고 증가 메서드
     */
    public void addStock(){
        this.stockQuantity++;
    }

    public void addStockAll(){
        this.allStockQuantity++;
        this.stockQuantity++;
    }

    /**
     * 재고 감소 메서드
     */
    public void removeStock(){
        if(this.stockQuantity==0){
            throw new NotEnoughStockException("재고가 부족합니다.");
        }
        this.stockQuantity--;
    }

    public void updateInfo(String name, int allStockQuantity, String category){
        this.name = name;
        this.stockQuantity += (allStockQuantity - this.allStockQuantity);
        this.allStockQuantity = allStockQuantity;
        this.category = category;
    }


}
