import SwiftUI
import Combine
import shared

struct ScoringTypeOption: Identifiable {
    let id = UUID()
    let name: String
    let type: ScoringType
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
        self.selectedTypeName = UserDefaults.standard.string(forKey: "selectedScoringType") ?? "Advantage"
    }

    let scoringTypes: [ScoringTypeOption] = [
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
}

