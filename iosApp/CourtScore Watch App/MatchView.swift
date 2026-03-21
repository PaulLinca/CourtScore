import SwiftUI
import Combine
import shared

struct MatchView: View {
    @StateObject private var viewModel: MatchViewModelWrapper
    @StateObject private var colorSchemeManager = ColorSchemeManager.shared
    @Environment(\.dismiss) var dismiss
    @State private var showSetAnimation = false

    init(sport: Sport = Sport.padel) {
        _viewModel = StateObject(wrappedValue: MatchViewModelWrapper(sport: sport))
    }

    // Detect screen size for responsive layout
    private var isSmallWatch: Bool {
        WKInterfaceDevice.current().screenBounds.width <= 176 // 40mm and smaller watches (162-176px)
    }

    // Adaptive sizing based on screen size
    private var vStackSpacing: CGFloat { isSmallWatch ? 8 : 12 }
    private var horizontalPadding: CGFloat { isSmallWatch ? 8 : 12 }
    private var bottomPadding: CGFloat { isSmallWatch ? 8 : 12 }
    private var topPadding: CGFloat { isSmallWatch ? 8 : 0 }
    private var gameScoreSpacing: CGFloat { isSmallWatch ? 8 : 12 }
    private var gameScoreHeight: CGFloat { isSmallWatch ? 70 : 90 }
    private var bottomButtonSpacing: CGFloat { isSmallWatch ? 12 : 20 }
    private var bottomButtonSize: CGFloat { isSmallWatch ? 26 : 30 }
    private var bottomButtonIconSize: CGFloat { isSmallWatch ? 14 : 16 }
    private var scoreFontSize: CGFloat { isSmallWatch ? 40 : 48 }
    private var scoreCornerRadius: CGFloat { isSmallWatch ? 10 : 12 }

    private var setWinnerColor: Color {
        guard let winner = viewModel.uiState.setWinner?.int32Value else {
            return Color(hex: "eeeff0")
        }
        return winner == 1 ? colorSchemeManager.playerOneColor : colorSchemeManager.playerTwoColor
    }

    var body: some View {
        ZStack {
            Color(hex: "000000")
                .ignoresSafeArea()

            VStack(spacing: vStackSpacing) {
                HStack(alignment: .center, spacing: 0) {
                    Spacer()

                    if viewModel.uiState.showServingIndicator {
                        ServingIndicator(
                            isPlayerOneServing: viewModel.uiState.playerOneServing,
                            onToggleServing: viewModel.toggleServing,
                            enabled: !viewModel.uiState.isFinished,
                            playerOneColor: colorSchemeManager.playerOneColor,
                            playerTwoColor: colorSchemeManager.playerTwoColor
                        )
                    }

                    if viewModel.uiState.showSubScores {
                        ScoreTable(
                            player1SetScores: viewModel.uiState.playerOneSetScores.map { $0.int32Value },
                            player2SetScores: viewModel.uiState.playerTwoSetScores.map { $0.int32Value },
                            playerOneColor: colorSchemeManager.playerOneColor,
                            playerTwoColor: colorSchemeManager.playerTwoColor
                        )
                    }

                    WinnerIndicator(
                        playerOneWon: viewModel.uiState.playerOneWon,
                        playerTwoWon: viewModel.uiState.playerTwoWon,
                        playerOneColor: colorSchemeManager.playerOneColor,
                        playerTwoColor: colorSchemeManager.playerTwoColor
                    )

                    Spacer()
                }
                .padding(.top, topPadding)

                CurrentGameScore(
                    player1GameScore: viewModel.uiState.playerOneGameScore,
                    player2GameScore: viewModel.uiState.playerTwoGameScore,
                    onPlayer1Score: viewModel.onPlayerOneScored,
                    onPlayer2Score: viewModel.onPlayerTwoScored,
                    enabled: !viewModel.uiState.isFinished,
                    playerOneColor: colorSchemeManager.playerOneColor,
                    playerTwoColor: colorSchemeManager.playerTwoColor,
                    gameWinner: viewModel.uiState.gameWinner?.int32Value,
                    goldenWinner: viewModel.showGoldenPointAnimation ? viewModel.goldenWinnerPlayer : 0,
                    onAnimationComplete: viewModel.onAnimationComplete,
                    spacing: gameScoreSpacing,
                    height: gameScoreHeight,
                    fontSize: scoreFontSize,
                    cornerRadius: scoreCornerRadius,
                    serveFromRight: viewModel.uiState.showServingIndicator ? viewModel.uiState.serveFromRight : nil,
                    serveColor: viewModel.uiState.playerOneServing ? colorSchemeManager.playerOneColor : colorSchemeManager.playerTwoColor
                )
                                
                HStack(spacing: bottomButtonSpacing) {
                    Button(action: viewModel.onUndo) {
                        Image(systemName: "arrow.uturn.backward")
                            .font(.system(size: bottomButtonIconSize))
                            .foregroundColor(viewModel.uiState.isFinished ? Color(hex: "aaaab1").opacity(0.3) : Color(hex: "aaaab1"))
                            .frame(width: bottomButtonSize, height: bottomButtonSize)
                    }
                    .disabled(viewModel.uiState.isFinished)
                    .buttonStyle(.plain)
                    .background(viewModel.uiState.isFinished ? Color(hex: "222327").opacity(0.3) : Color(hex: "222327"))
                    .clipShape(Circle())

                    Button(action: viewModel.onFinishClicked) {
                        Image(systemName: "flag.checkered")
                            .font(.system(size: bottomButtonIconSize))
                            .foregroundColor(viewModel.uiState.isFinished ? Color(hex: "aaaab1").opacity(0.3) : Color(hex: "aaaab1"))
                            .frame(width: bottomButtonSize, height: bottomButtonSize)
                    }
                    .disabled(viewModel.uiState.isFinished)
                    .buttonStyle(.plain)
                    .background(viewModel.uiState.isFinished ? Color(hex: "222327").opacity(0.3) : Color(hex: "222327"))
                    .clipShape(Circle())
                }
            }
            .padding(.horizontal, horizontalPadding)
            .padding(.bottom, bottomPadding)

            if viewModel.uiState.setWinner != nil {
                ZStack {
                    Color(hex: "000000")
                        .opacity(0.85)
                        .ignoresSafeArea()
                        .transition(.opacity)

                    ZStack {
                        Circle()
                            .fill(setWinnerColor.opacity(0.15))
                            .frame(width: 100, height: 100)
                            .scaleEffect(showSetAnimation ? 1.5 : 0.5)
                            .opacity(showSetAnimation ? 0.0 : 0.3)
                            .animation(.easeOut(duration: 0.8), value: showSetAnimation)

                        VStack(spacing: 12) {
                            // Tennis ball with rotation and bounce (2 full rotations for visibility)
                            Image(systemName: "tennisball.fill")
                                .font(.system(size: 36))
                                .foregroundColor(setWinnerColor)
                                .shadow(color: setWinnerColor.opacity(0.5), radius: 8, x: 0, y: 0)
                                .rotationEffect(.degrees(showSetAnimation ? 720 : 0))
                                .scaleEffect(showSetAnimation ? 1.0 : 0.3)
                                .animation(.spring(response: 0.7, dampingFraction: 0.5), value: showSetAnimation)

                            // "Set!" text with multiple animation effects
                            Text("set_won".localized())
                                .font(.system(size: 32, weight: .bold))
                                .foregroundColor(setWinnerColor)
                                .shadow(color: setWinnerColor.opacity(0.3), radius: 4, x: 0, y: 2)
                                .scaleEffect(showSetAnimation ? 1.0 : 0.5)
                                .opacity(showSetAnimation ? 1.0 : 0.0)
                                .animation(.spring(response: 0.5, dampingFraction: 0.7).delay(0.1), value: showSetAnimation)
                        }
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
                .transition(.asymmetric(
                    insertion: .scale(scale: 0.7).combined(with: .opacity),
                    removal: .scale(scale: 1.2).combined(with: .opacity)
                ))
                .onAppear {
                    withAnimation {
                        showSetAnimation = true
                    }
                }
                .onDisappear {
                    showSetAnimation = false
                }
            }

            if viewModel.showScoringTypeDialog {
                ZStack {
                    Color(hex: "000000")
                        .ignoresSafeArea()

                    VStack(spacing: 8) {
                        Text("deuce_choose_type".localized())
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(Color(hex: "eeeff0"))
                            .multilineTextAlignment(.center)
                            .padding(.bottom, 2)

                        Button(action: { viewModel.onScoringTypeSelected(typeName: "Advantage") }) {
                            Text("advantage".localized())
                                .font(.system(size: 15, weight: .medium))
                                .foregroundColor(Color(hex: "eeeff0"))
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                        }
                        .buttonStyle(.plain)
                        .background(Color(hex: "222327"))
                        .cornerRadius(10)

                        Button(action: { viewModel.onScoringTypeSelected(typeName: "Golden Point") }) {
                            Text("Golden Point")
                                .font(.system(size: 15, weight: .medium))
                                .foregroundColor(Color(hex: "eeeff0"))
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                        }
                        .buttonStyle(.plain)
                        .background(Color(hex: "222327"))
                        .cornerRadius(10)

                    }
                    .padding(.horizontal, 10)
                }
                .transition(.opacity)
            }
        }
        .alert("finish_dialog_title".localized(), isPresented: Binding(
            get: { viewModel.uiState.showFinishDialog },
            set: { if !$0 { viewModel.onFinishCancelled() } }
        )) {
            Button("no".localized(), role: .cancel) {
                viewModel.onFinishCancelled()
            }
            Button("yes".localized()) {
                viewModel.onFinishConfirmed()
            }
        }
        .alert("back_dialog_title".localized(), isPresented: Binding(
            get: { viewModel.uiState.showBackDialog },
            set: { if !$0 { viewModel.onBackCancelled() } }
        )) {
            Button("no".localized(), role: .cancel) {
                viewModel.onBackCancelled()
            }
            Button("yes".localized(), role: .destructive) {
                viewModel.onBackConfirmed()
                dismiss()
            }
        }
        .onChange(of: viewModel.uiState.setWinner) { newValue in
            if newValue != nil {
                DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                    viewModel.onSetAnimationComplete()
                }
            }
        }
        .onChange(of: viewModel.showGoldenPointAnimation) { newValue in
            if newValue {
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                    viewModel.onGoldenPointAnimationComplete()
                }
            }
        }
        .navigationBarBackButtonHidden(!viewModel.uiState.isFinished)
        .toolbar {
            if !viewModel.uiState.isFinished {
                ToolbarItem(placement: .cancellationAction) {
                    Button {
                        let handled = viewModel.onBackPressed()
                        if !handled {
                            dismiss()
                        }
                    } label: {
                        Image(systemName: "chevron.left")
                            .foregroundColor(Color(hex: "1E8FD5"))
                    }
                }
            }
        }
    }
}

// MARK: - Current Game Score Component
struct CurrentGameScore: View {
    let player1GameScore: String
    let player2GameScore: String
    let onPlayer1Score: () -> Void
    let onPlayer2Score: () -> Void
    let enabled: Bool
    let playerOneColor: Color
    let playerTwoColor: Color
    let gameWinner: Int32?
    let goldenWinner: Int
    let onAnimationComplete: () -> Void
    let spacing: CGFloat
    let height: CGFloat
    let fontSize: CGFloat
    let cornerRadius: CGFloat
    var serveFromRight: Bool? = nil
    var serveColor: Color = .white

    var body: some View {
        VStack(spacing: 4) {
            HStack(spacing: spacing) {
                GameScoreButton(
                    score: player1GameScore,
                    onClick: onPlayer1Score,
                    enabled: enabled,
                    primaryColor: playerOneColor,
                    showWinAnimation: gameWinner != nil,
                    isGoldenPointWin: goldenWinner == 1,
                    onAnimationComplete: onAnimationComplete,
                    fontSize: fontSize,
                    cornerRadius: cornerRadius
                )

                GameScoreButton(
                    score: player2GameScore,
                    onClick: onPlayer2Score,
                    enabled: enabled,
                    primaryColor: playerTwoColor,
                    showWinAnimation: gameWinner != nil,
                    isGoldenPointWin: goldenWinner == 2,
                    onAnimationComplete: onAnimationComplete,
                    fontSize: fontSize,
                    cornerRadius: cornerRadius
                )
            }
            .frame(height: height)

            if let serveFromRight = serveFromRight {
                HStack(spacing: spacing) {
                    Spacer()
                    Circle()
                        .fill(serveFromRight ? Color.clear : serveColor)
                        .frame(width: 6, height: 6)
                    Spacer()
                    Circle()
                        .fill(serveFromRight ? serveColor : Color.clear)
                        .frame(width: 6, height: 6)
                    Spacer()
                }
            }
        }
    }
}

// MARK: - Game Score Button
struct GameScoreButton: View {
    let score: String
    let onClick: () -> Void
    let enabled: Bool
    let primaryColor: Color
    let showWinAnimation: Bool
    let isGoldenPointWin: Bool
    let onAnimationComplete: () -> Void
    let fontSize: CGFloat
    let cornerRadius: CGFloat

    @State private var animationTrigger = UUID()

    private let goldenColor = Color(hex: "FFD700")

    var body: some View {
        Button(action: onClick) {
            ZStack {
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(
                        isGoldenPointWin ? goldenColor : (enabled ? primaryColor : primaryColor.opacity(0.3)),
                        lineWidth: isGoldenPointWin ? 3 : 2
                    )
                    .background(Color.clear)

                if isGoldenPointWin {
                    RoundedRectangle(cornerRadius: cornerRadius)
                        .fill(goldenColor.opacity(0.18))
                        .transition(.opacity)
                }

                // Use AnimatedContent-like behavior with transition
                ZStack {
                    if showWinAnimation && score == "0" {
                        Text(score)
                            .font(.system(size: fontSize, weight: .bold))
                            .foregroundColor(enabled ? primaryColor : primaryColor.opacity(0.3))
                            .transition(.asymmetric(
                                insertion: .move(edge: .top).combined(with: .opacity),
                                removal: .move(edge: .bottom).combined(with: .opacity)
                            ))
                            .id(animationTrigger)
                    } else {
                        Text(score)
                            .font(.system(size: fontSize, weight: .bold))
                            .foregroundColor(enabled ? primaryColor : primaryColor.opacity(0.3))
                    }
                }
                .animation(.easeInOut(duration: 0.4), value: animationTrigger)
            }
        }
        .disabled(!enabled)
        .buttonStyle(.plain)
        .animation(.easeInOut(duration: 0.3), value: isGoldenPointWin)
        .onChange(of: showWinAnimation) { newValue in
            if newValue {
                animationTrigger = UUID()
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.7) {
                    onAnimationComplete()
                }
            }
        }
    }
}

// MARK: - Score Table
struct ScoreTable: View {
    let player1SetScores: [Int32]
    let player2SetScores: [Int32]
    let playerOneColor: Color
    let playerTwoColor: Color

    var body: some View {
        VStack(spacing: 2) {
            HStack(spacing: 4) {
                ForEach(Array(player1SetScores.enumerated()), id: \.offset) { _, score in
                    Text("\(score)")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(playerOneColor)
                        .padding(.horizontal, 4)
                }
            }

            HStack(spacing: 4) {
                ForEach(Array(player2SetScores.enumerated()), id: \.offset) { _, score in
                    Text("\(score)")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(playerTwoColor)
                        .padding(.horizontal, 4)
                }
            }
        }
    }
}

// MARK: - Serving Indicator
struct ServingIndicator: View {
    let isPlayerOneServing: Bool
    let onToggleServing: () -> Void
    let enabled: Bool
    let playerOneColor: Color
    let playerTwoColor: Color

    var body: some View {
        Button(action: onToggleServing) {
            VStack(spacing: 2) {
                Image(systemName: "tennisball.fill")
                    .font(.system(size: 10))
                    .foregroundColor(isPlayerOneServing ?
                        (enabled ? playerOneColor : playerOneColor.opacity(0.3)) :
                        Color(hex: "aaaab1").opacity(0.3))

                Spacer()

                Image(systemName: "tennisball.fill")
                    .font(.system(size: 10))
                    .foregroundColor(!isPlayerOneServing ?
                        (enabled ? playerTwoColor : playerTwoColor.opacity(0.3)) :
                        Color(hex: "aaaab1").opacity(0.3))
            }
            .frame(width: 24, height: 24)
        }
        .disabled(!enabled)
        .buttonStyle(.plain)
    }
}

// MARK: - Winner Indicator
struct WinnerIndicator: View {
    let playerOneWon: Bool
    let playerTwoWon: Bool
    let playerOneColor: Color
    let playerTwoColor: Color

    var body: some View {
        ZStack {
            if playerOneWon {
                Image(systemName: "crown.fill")
                    .font(.system(size: 15))
                    .foregroundColor(playerOneColor)
            } else if playerTwoWon {
                Image(systemName: "crown.fill")
                    .font(.system(size: 15))
                    .foregroundColor(playerTwoColor)
            }
        }
        .frame(width: 24, height: 24)
    }
}

// MARK: - ViewModel Wrapper
class MatchViewModelWrapper: ObservableObject {
    private let viewModel: MatchViewModel
    @Published var uiState: MatchUiState
    @Published var showScoringTypeDialog = false
    @Published var showGoldenPointAnimation = false
    @Published var goldenWinnerPlayer: Int = 0
    private var scoringTypeChosen = false
    private var chosenTypeName = ""
    private var wasAtGoldenPointDeuce = false
    private let sport: Sport
    private var timer: Timer?

    init(sport: Sport = Sport.padel) {
        self.sport = sport
        // Use companion factory methods to avoid exposing SportStrategy at the ObjC boundary.
        self.viewModel = sport == Sport.football
            ? MatchViewModel.companion.forFootball()
            : MatchViewModel.companion.forPadel()
        self.uiState = viewModel.uiState.value as! MatchUiState

        // Apply saved scoring type for padel if one is set; football ignores this.
        if sport == Sport.padel && !ScoringTypeManager.shared.isAskEveryTime {
            applyScoringType(ScoringTypeManager.shared.selectedTypeName)
            scoringTypeChosen = true
        }

        // Poll for state changes
        timer = Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            let newState = self.viewModel.uiState.value as! MatchUiState
            if self.hasStateChanged(newState) {
                DispatchQueue.main.async {
                    self.applyNewState(newState)
                }
            }
        }
    }

    deinit {
        timer?.invalidate()
    }

    private func hasStateChanged(_ newState: MatchUiState) -> Bool {
        return newState.playerOneGameScore != uiState.playerOneGameScore
            || newState.playerTwoGameScore != uiState.playerTwoGameScore
            || newState.isFinished != uiState.isFinished
            || newState.showFinishDialog != uiState.showFinishDialog
            || newState.showBackDialog != uiState.showBackDialog
            || newState.gameWinner != uiState.gameWinner
            || newState.setWinner != uiState.setWinner
            || newState.serveFromRight != uiState.serveFromRight
            || newState.playerOneServing != uiState.playerOneServing
    }

    // Called from the in-match dialog — typeName is "Advantage", "Golden Point", or "Star Point"
    func onScoringTypeSelected(typeName: String) {
        applyScoringType(typeName)
        scoringTypeChosen = true
        withAnimation(.easeInOut(duration: 0.3)) {
            showScoringTypeDialog = false
        }
        updateState()
    }

    func onUndo() {
        viewModel.onUndo()
        // If the dialog was open, undo means we're no longer at deuce — close it
        if showScoringTypeDialog {
            withAnimation(.easeInOut(duration: 0.3)) {
                showScoringTypeDialog = false
            }
            scoringTypeChosen = false
        }
        wasAtGoldenPointDeuce = false
        updateState()
    }

    func onPlayerOneScored() {
        wasAtGoldenPointDeuce = isAtGoldenPointDeuce()
        viewModel.onPlayerOneScored()
        updateState()
    }

    func onPlayerTwoScored() {
        wasAtGoldenPointDeuce = isAtGoldenPointDeuce()
        viewModel.onPlayerTwoScored()
        updateState()
    }

    private func isAtGoldenPointDeuce() -> Bool {
        return chosenTypeName == "Golden Point"
            && uiState.playerOneGameScore == "40"
            && uiState.playerTwoGameScore == "40"
    }

    func onGoldenPointAnimationComplete() {
        showGoldenPointAnimation = false
        goldenWinnerPlayer = 0
        wasAtGoldenPointDeuce = false
    }
    func toggleServing() { viewModel.toggleServing(); updateState() }
    func onFinishClicked() { viewModel.onFinishClicked(); updateState() }
    func onFinishConfirmed() { viewModel.onFinishConfirmed(); updateState() }
    func onFinishCancelled() { viewModel.onFinishCancelled(); updateState() }
    func onAnimationComplete() { viewModel.onAnimationComplete(); updateState() }
    func onSetAnimationComplete() { viewModel.onSetAnimationComplete(); updateState() }

    func onBackPressed() -> Bool {
        let result = viewModel.onBackPressed()
        updateState()
        return result
    }

    func onBackConfirmed() { viewModel.onBackConfirmed(); updateState() }
    func onBackCancelled() { viewModel.onBackCancelled(); updateState() }

    private func applyScoringType(_ name: String) {
        chosenTypeName = name
        switch name {
        case "Advantage":    viewModel.setScoringTypeAdvantage()
        case "Golden Point": viewModel.setScoringTypeGoldenPoint()
        case "Star Point":   viewModel.setScoringTypeStarPoint()
        default: break
        }
    }

    private func updateState() {
        DispatchQueue.main.async {
            self.applyNewState(self.viewModel.uiState.value as! MatchUiState)
        }
    }

    private func applyNewState(_ newState: MatchUiState) {
        let previousGameWinner = uiState.gameWinner
        uiState = newState
        if !scoringTypeChosen
            && newState.playerOneGameScore == "40"
            && newState.playerTwoGameScore == "40" {
            withAnimation(.easeInOut(duration: 0.4)) {
                showScoringTypeDialog = true
            }
        }
        if wasAtGoldenPointDeuce && newState.gameWinner != nil && previousGameWinner == nil {
            goldenWinnerPlayer = Int(newState.gameWinner!.int32Value)
            showGoldenPointAnimation = true
            wasAtGoldenPointDeuce = false
        }
    }
}

