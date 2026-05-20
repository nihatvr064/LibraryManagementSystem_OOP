package az.library.model.member;

import az.library.model.Member;

public class GoldMember extends Member {

    public GoldMember(String memberId, String name) {
        super(memberId, name);
    }

    @Override
    public int getMaxBorrowLimit() {
        return 6;
    }

}
