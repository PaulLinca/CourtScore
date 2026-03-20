import SwiftUI
import shared

struct SportSelectView: View {
    private var isSmallWatch: Bool {
        WKInterfaceDevice.current().screenBounds.width <= 176
    }

    var body: some View {
        List {
            Text("select_sport".localized())
                .font(.headline)
                .foregroundColor(.white)
                .listRowBackground(Color.black)

            NavigationLink(destination: MatchView(sport: Sport.padel)) {
                Label("sport_padel".localized(), systemImage: "tennisball.fill")
                    .foregroundColor(.white)
            }
            .listRowBackground(Color(hex: "1a1a1a"))

            NavigationLink(destination: MatchView(sport: Sport.football)) {
                Label("sport_football".localized(), systemImage: "soccerball")
                    .foregroundColor(.white)
            }
            .listRowBackground(Color(hex: "1a1a1a"))
        }
        .background(Color.black)
    }
}
