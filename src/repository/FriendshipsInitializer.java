package repository;

import domain.Friendship;

public class FriendshipsInitializer {
    UserRepository userRepository;
    FriendshipRepository friendshipRepository;

    public FriendshipsInitializer(UserRepository userRepository, FriendshipRepository fRepo) {
        this.userRepository = userRepository;
        this.friendshipRepository = fRepo;
        initialize();
    }

    private void initialize() {
        for (Friendship f : friendshipRepository.getAll())
            userRepository.addFriends(f.getFirst(), f.getSecond());
    }
}
