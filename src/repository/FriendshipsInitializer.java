package repository;

import domain.Friendship;

public class FriendshipsInitializer {
    UserRepository uRepo;
    FriendshipRepository fRepo;

    public FriendshipsInitializer(UserRepository uRepo, FriendshipRepository fRepo) {
        this.uRepo = uRepo;
        this.fRepo = fRepo;
        initialize();
    }

    private void initialize() {
        for (Friendship f : fRepo.getAll())
            uRepo.addFriends(f.getFirst(), f.getSecond());
    }
}
