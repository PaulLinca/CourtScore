import SwiftUI
import Combine

struct ScoringTypeOption: Identifiable {
    let id = UUID()
    let name: String
    let description: String
}

class ScoringTypeManager: ObservableObject {
    static let shared = ScoringTypeManager()

    @Published var selectedTypeName: String {
        didSet {
            UserDefaults.standard.set(selectedTypeName, forKey: "selectedScoringType")
        }
    }

    private init() {
        self.selectedTypeName = UserDefaults.standard.string(forKey: "selectedScoringType") ?? "Ask Every Time"
    }

    let scoringTypes: [ScoringTypeOption] = [
        ScoringTypeOption(name: "Ask Every Time", description: "scoring_ask_every_time_desc".localized()),
        ScoringTypeOption(name: "Advantage", description: "scoring_advantage_desc".localized()),
        ScoringTypeOption(name: "Golden Point", description: "scoring_golden_point_desc".localized()),
        ScoringTypeOption(name: "Star Point", description: "scoring_star_point_desc".localized())
    ]

    var isAskEveryTime: Bool {
        selectedTypeName == "Ask Every Time"
    }
}
