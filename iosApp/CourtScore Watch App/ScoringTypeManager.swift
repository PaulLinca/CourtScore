import SwiftUI
import Combine
import shared

struct ScoringTypeOption: Identifiable {
    let id = UUID()
    let name: String
    let type: ScoringType? // nil = ask every time
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
        ScoringTypeOption(
            name: "Ask Every Time",
            type: nil,
            description: "scoring_ask_every_time_desc".localized()
        ),
        ScoringTypeOption(
            name: "Advantage",
            type: ScoringType.advantage,
            description: "scoring_advantage_desc".localized()
        ),
        ScoringTypeOption(
            name: "Golden Point",
            type: ScoringType.goldenPoint,
            description: "scoring_golden_point_desc".localized()
        ),
        ScoringTypeOption(
            name: "Star Point",
            type: ScoringType.starPoint,
            description: "scoring_star_point_desc".localized()
        )
    ]

    var selectedType: ScoringTypeOption {
        scoringTypes.first { $0.name == selectedTypeName } ?? scoringTypes[0]
    }

    // nil = ask every time
    var selectedScoringType: ScoringType? {
        selectedType.type
    }
}

