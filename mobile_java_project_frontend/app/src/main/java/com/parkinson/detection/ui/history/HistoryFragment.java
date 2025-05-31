package com.parkinson.detection.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.parkinson.detection.R;
import com.parkinson.detection.api.ApiClient;
import com.parkinson.detection.api.SessionApiService;
import com.parkinson.detection.model.Session;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyView;
    private TabLayout tabLayout;
    private SessionAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        
        // Find views
        recyclerView = root.findViewById(R.id.recycler_view);
        swipeRefresh = root.findViewById(R.id.swipe_refresh);
        progressBar = root.findViewById(R.id.progress_bar);
        emptyView = root.findViewById(R.id.empty_view);
        tabLayout = root.findViewById(R.id.tab_layout);
        
        // Create adapter
        adapter = new SessionAdapter(new ArrayList<>(), this::navigateToSessionDetail);
        recyclerView.setAdapter(adapter);
        
        // Initialize the ViewModel
        SessionApiService apiService = ApiClient.getInstance().createService(SessionApiService.class);
        historyViewModel = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new HistoryViewModel(requireActivity().getApplication(), apiService);
            }
        }.create(HistoryViewModel.class);
        
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Set up swipe refresh
        swipeRefresh.setOnRefreshListener(() -> {
            historyViewModel.refreshSessions();
            swipeRefresh.setRefreshing(false);
        });
        
        // Set up tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        historyViewModel.clearFilter();
                        break;
                    case 1:
                        historyViewModel.setFilter(0); // No Parkinson's
                        break;
                    case 2:
                        historyViewModel.setFilter(1); // Suspected Parkinson's
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
        
        // Observe sessions data
        historyViewModel.getSessions().observe(getViewLifecycleOwner(), sessions -> {
            progressBar.setVisibility(View.GONE);
            if (sessions.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateSessions(sessions);
            }
        });
    }
    
    /**
     * Navigate to session detail
     */
    private void navigateToSessionDetail(Session session) {
        NavController navController = Navigation.findNavController(requireView());
        Bundle args = new Bundle();
        args.putLong("recordingId", session.getId());
        navController.navigate(R.id.action_navigation_history_to_recordingDetailFragment, args);
    }
    
    /**
     * Adapter for the sessions list
     */
    private static class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
        
        private List<Session> sessions;
        private final SessionClickListener clickListener;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - HH:mm", Locale.getDefault());
        
        public SessionAdapter(List<Session> sessions, SessionClickListener clickListener) {
            this.sessions = sessions;
            this.clickListener = clickListener;
        }
        
        @NonNull
        @Override
        public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_session, parent, false);
            return new SessionViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
            Session session = sessions.get(position);
            holder.textDate.setText(session.getTimestamp().format(formatter));
            
            // Format and display the duration
            if (session.getDurationMs() != null) {
                long durationSeconds = session.getDurationMs() / 1000;
                int minutes = (int) (durationSeconds / 60);
                int seconds = (int) (durationSeconds % 60);
                holder.textDuration.setText(String.format("%02d:%02d", minutes, seconds));
            } else {
                holder.textDuration.setText("--:--");
            }
            
            holder.itemView.setOnClickListener(v -> clickListener.onSessionClick(session));
        }
        
        @Override
        public int getItemCount() {
            return sessions.size();
        }
        
        public void updateSessions(List<Session> newSessions) {
            this.sessions = newSessions;
            notifyDataSetChanged();
        }
        
        /**
         * ViewHolder for sessions
         */
        static class SessionViewHolder extends RecyclerView.ViewHolder {
            TextView textDate;
            TextView textDuration;
            
            public SessionViewHolder(@NonNull View itemView) {
                super(itemView);
                textDate = itemView.findViewById(R.id.text_date);
                textDuration = itemView.findViewById(R.id.text_duration);
            }
        }
        
        /**
         * Interface for session click events
         */
        interface SessionClickListener {
            void onSessionClick(Session session);
        }
    }
} 