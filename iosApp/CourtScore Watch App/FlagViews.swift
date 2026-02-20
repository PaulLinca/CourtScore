import SwiftUI

// MARK: - Flag Views using Image Assets
struct UKFlagView: View {
    var body: some View {
        Image("flag_uk")
            .resizable()
            .aspectRatio(contentMode: .fit)
    }
}

struct SpainFlagView: View {
    var body: some View {
        Image("flag_spain")
            .resizable()
            .aspectRatio(contentMode: .fit)
    }
}

struct CataloniaFlagView: View {
    var body: some View {
        Image("flag_catalonia")
            .resizable()
            .aspectRatio(contentMode: .fit)
    }
}

