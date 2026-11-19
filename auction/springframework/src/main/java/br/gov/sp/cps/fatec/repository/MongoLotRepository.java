package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.repository.LotRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/** Adaptador: implementa a porta do domínio usando MongoTemplate. As operações críticas são atômicas. */
@Component
class MongoLotRepository implements LotRepository {

    private final MongoTemplate mongo;

    MongoLotRepository(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public long count() {
        return mongo.count(new Query(), LotDocument.class);
    }

    @Override
    public Lot insert(Lot lot) {
        return mongo.insert(LotDocument.from(lot)).toDomain();
    }

    @Override
    public Optional<Lot> findById(String id) {
        return Optional.ofNullable(mongo.findById(id, LotDocument.class)).map(LotDocument::toDomain);
    }

    @Override
    public List<Lot> findOpen(Instant now) {
        Query q = new Query(where("status").is(Lot.Status.OPEN).and("endsAt").gt(now)).with(Sort.by("endsAt"));
        return mongo.find(q, LotDocument.class).stream().map(LotDocument::toDomain).toList();
    }

    @Override
    public List<Lot> findExpired(Instant now) {
        Query q = new Query(where("status").is(Lot.Status.OPEN).and("endsAt").lte(now));
        return mongo.find(q, LotDocument.class).stream().map(LotDocument::toDomain).toList();
    }

    @Override
    public List<Lot> findClosed() {
        Query q = new Query(where("status").ne(Lot.Status.OPEN)).with(Sort.by(Sort.Direction.DESC, "endsAt"));
        return mongo.find(q, LotDocument.class).stream().map(LotDocument::toDomain).toList();
    }

    @Override
    public List<Lot> findClosedWithBidFrom(String userId, int limit) {
        Query q = new Query(where("status").ne(Lot.Status.OPEN).and("bids.userId").is(userId))
                .with(Sort.by(Sort.Direction.DESC, "endsAt")).limit(limit);
        return mongo.find(q, LotDocument.class).stream().map(LotDocument::toDomain).toList();
    }

    @Override
    public List<Lot> findClosedWithPendingEvent() {
        Query q = new Query(where("status").ne(Lot.Status.OPEN).and("closedEventSent").ne(true));
        return mongo.find(q, LotDocument.class).stream().map(LotDocument::toDomain).toList();
    }

    @Override
    public void markClosedEventSent(String lotId) {
        mongo.updateFirst(new Query(where("_id").is(lotId)), new Update().set("closedEventSent", true), LotDocument.class);
    }

    @Override
    public Optional<Lot> tryPlaceBid(Lot lot, String userId, String userName, long amount, Instant now) {
        Query q = new Query(where("_id").is(lot.id())
                .and("status").is(Lot.Status.OPEN)
                .and("endsAt").gt(now)
                .and("startPrice").lte(amount)
                .and("currentBid").lte(amount - lot.minIncrement())
                .and("leaderId").ne(userId));
        Update u = new Update()
                .set("currentBid", amount)
                .set("leaderId", userId)
                .set("leaderName", userName)
                .push("bids", new LotDocument.BidDocument(userId, userName, amount, now));
        LotDocument before = mongo.findAndModify(q, u, FindAndModifyOptions.options().returnNew(false), LotDocument.class);
        return Optional.ofNullable(before).map(LotDocument::toDomain);
    }

    @Override
    public Optional<Lot> tryClose(String lotId, Lot.Status newStatus, long finalPrice) {
        Query q = new Query(where("_id").is(lotId).and("status").is(Lot.Status.OPEN));
        Update u = new Update().set("status", newStatus).set("finalPrice", finalPrice);
        LotDocument closed = mongo.findAndModify(q, u, FindAndModifyOptions.options().returnNew(true), LotDocument.class);
        return Optional.ofNullable(closed).map(LotDocument::toDomain);
    }
}
