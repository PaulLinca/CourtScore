import SwiftUI
import Combine

class ColorSchemeManager: ObservableObject {
    static let shared = ColorSchemeManager()

    @Published var selectedSchemeName: String {
        didSet {
            UserDefaults.standard.set(selectedSchemeName, forKey: "selectedColorScheme")
        }
    }

    private init() {
        self.selectedSchemeName = UserDefaults.standard.string(forKey: "selectedColorScheme") ?? "Blue & Orange"
    }

    let colorSchemes: [ColorSchemeOption] = [
        ColorSchemeOption(name: "Teal & Coral", playerOneColor: Color(hex: "008080"), playerTwoColor: Color(hex: "FF7F50")),
        ColorSchemeOption(name: "Blue & Orange", playerOneColor: Color(hex: "5076FF"), playerTwoColor: Color(hex: "F8A464")),
        ColorSchemeOption(name: "Red & Cream", playerOneColor: Color(hex: "A4193D"), playerTwoColor: Color(hex: "FFDFB9")),
        ColorSchemeOption(name: "Pink & Yellow", playerOneColor: Color(hex: "d3687f"), playerTwoColor: Color(hex: "CBCE91")),
        ColorSchemeOption(name: "Cyan & Magenta", playerOneColor: Color(hex: "00BCD4"), playerTwoColor: Color(hex: "E91E63")),
        ColorSchemeOption(name: "Yellow & Red", playerOneColor: Color(hex: "FDD20E"), playerTwoColor: Color(hex: "c72d1b")),
        ColorSchemeOption(name: "Indigo & Lime", playerOneColor: Color(hex: "4831D4"), playerTwoColor: Color(hex: "CCF381"))
    ]

    var selectedScheme: ColorSchemeOption {
        colorSchemes.first { $0.name == selectedSchemeName } ?? colorSchemes[1]
    }

    var playerOneColor: Color {
        selectedScheme.playerOneColor
    }

    var playerTwoColor: Color {
        selectedScheme.playerTwoColor
    }
}
