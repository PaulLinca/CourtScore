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
        self.selectedSchemeName = UserDefaults.standard.string(forKey: "selectedColorScheme") ?? "Sunset & Ocean"
    }

    let colorSchemes: [ColorSchemeOption] = [
        ColorSchemeOption(name: "Sunset & Ocean", playerOneColor: Color(hex: "FF7E5F"), playerTwoColor: Color(hex: "00D4FF")),
        ColorSchemeOption(name: "Pink & Yellow", playerOneColor: Color(hex: "d3687f"), playerTwoColor: Color(hex: "CBCE91")),
        ColorSchemeOption(name: "Lavender & Peach", playerOneColor: Color(hex: "9B59B6"), playerTwoColor: Color(hex: "FFB347")),
        ColorSchemeOption(name: "Cyan & Magenta", playerOneColor: Color(hex: "00BCD4"), playerTwoColor: Color(hex: "E91E63")),
        ColorSchemeOption(name: "Yellow & Red", playerOneColor: Color(hex: "FDD20E"), playerTwoColor: Color(hex: "c72d1b")),
        ColorSchemeOption(name: "Mint & Coral", playerOneColor: Color(hex: "3EECAC"), playerTwoColor: Color(hex: "FF6B6B")),
        ColorSchemeOption(name: "Forest & Sky", playerOneColor: Color(hex: "27AE60"), playerTwoColor: Color(hex: "3498DB"))
    ]

    var selectedScheme: ColorSchemeOption {
        colorSchemes.first { $0.name == selectedSchemeName } ?? colorSchemes[0]
    }

    var playerOneColor: Color {
        selectedScheme.playerOneColor
    }

    var playerTwoColor: Color {
        selectedScheme.playerTwoColor
    }
}
