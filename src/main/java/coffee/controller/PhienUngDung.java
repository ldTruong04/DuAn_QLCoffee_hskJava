package coffee.controller;

import coffee.model.NhanVien;

public class PhienUngDung {
    private NhanVien currentUser;

    public NhanVien getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(NhanVien currentUser) {
        this.currentUser = currentUser;
    }
}
