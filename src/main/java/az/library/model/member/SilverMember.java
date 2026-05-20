package az.library.model.member;

import az.library.model.Member;

public class SilverMember extends Member {

    public SilverMember(String memberId, String name) {
        super(memberId, name);
    }

    @Override
    public int getMaxBorrowLimit() {
        return 4;
    }

}
