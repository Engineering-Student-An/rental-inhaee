package an.rentalinhaee.repository;

import an.rentalinhaee.domain.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RentalQueryRepository {

    private final EntityManager em;

    public Page<Rental> findByStuId_Status_ItemName(RentalSearch rentalSearch, Pageable pageable) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QRental rental = QRental.rental;
        QStudent student = QStudent.student;
        QItem item = QItem.item;

        List<Rental> results = query.select(rental)
                .from(rental)
                .join(rental.student, student).join(rental.item, item)
                .where(stuIdLike(rentalSearch.getStuId()), statusEq(rentalSearch.getRentalStatus()), itemNameLike(rentalSearch.getItemName()))
                .orderBy(rental.status.asc(), rental.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회 쿼리
        long total = query.selectFrom(rental)
                .join(rental.student, student)
                .join(rental.item, item)
                .where(
                        stuIdLike(rentalSearch.getStuId()),
                        statusEq(rentalSearch.getRentalStatus()),
                        itemNameLike(rentalSearch.getItemName())
                )
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private static BooleanExpression itemNameLike(String itemName) {
        if(!StringUtils.hasText(itemName)) return null;
        return QItem.item.name.like('%' + itemName + '%').or(QItem.item.category.like('%' + itemName + '%'));
    }

    private static BooleanExpression statusEq(RentalStatus rentalStatus) {
        if(rentalStatus == null) return null;
        return QRental.rental.status.eq(rentalStatus);
    }

    private static BooleanExpression stuIdLike(String stuId) {
        if(!StringUtils.hasText(stuId)) return null;
        return QStudent.student.stuId.like('%' + stuId + '%');
    }


}
