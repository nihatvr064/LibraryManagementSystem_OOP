package az.library.model.member;

import az.library.model.Member;

public class BasicMember extends Member {

    public BasicMember(String memberId, String name) {
        super(memberId, name);
    }

    @Override
    public int getMaxBorrowLimit() {
        return 2;
    }

}
