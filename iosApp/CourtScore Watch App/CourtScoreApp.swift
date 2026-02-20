//
//  CourtScoreApp.swift
//  CourtScore Watch App
//
//  Created by Paul Tudor Linca on 14/2/26.
//  Copyright © 2026 orgName. All rights reserved.
//

import SwiftUI
import shared

@main
struct CourtScore_Watch_AppApp: App {
    @StateObject private var languageManager = LanguageManager.shared

    var body: some Scene {
        WindowGroup {
            MainScreen()
                .environment(\.locale, .init(identifier: languageManager.selectedLanguageCode))
        }
    }
}
