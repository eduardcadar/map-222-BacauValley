package repository;

import domain.Friendship;

public class FriendshipsInitializer {
    UserRepository userRepository;
    FriendshipRepository friendshipRepositoryRepo;

    public FriendshipsInitializer(UserRepository userRepository, FriendshipRepository fRepo) {
        this.userRepository = userRepository;
        this.friendshipRepositoryRepo = fRepo;
        initialize();
    }

    private void initialize() {
        for (Friendship f : friendshipRepositoryRepo.getAll())
            userRepository.addFriends(f.getFirst(), f.getSecond());
    }
}
