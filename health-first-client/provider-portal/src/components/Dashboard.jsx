import { 
  Container, 
  Typography, 
  Box, 
  AppBar, 
  Toolbar, 
  Button, 
  Grid, 
  Card, 
  CardContent,
  Fade,
  Slide
} from '@mui/material';
import { 
  LocalHospital, 
  People, 
  Schedule, 
  Assignment,
  Logout,
  Dashboard as DashboardIcon
} from '@mui/icons-material';

const Dashboard = ({ onLogout }) => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static" sx={{ background: 'linear-gradient(45deg, #2e7d32, #4caf50)' }}>
        <Toolbar>
          <LocalHospital sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Provider Portal
          </Typography>
          <Button color="inherit" startIcon={<DashboardIcon />}>
            Dashboard
          </Button>
          <Button color="inherit" startIcon={<People />}>
            Patients
          </Button>
          <Button color="inherit" onClick={onLogout} startIcon={<Logout />}>
            Logout
          </Button>
        </Toolbar>
      </AppBar>
      
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Slide direction="up" in={true} timeout={800}>
          <Box>
            <Fade in={true} timeout={1000}>
              <Typography variant="h3" component="h1" gutterBottom sx={{ 
                fontWeight: 700,
                background: 'linear-gradient(45deg, #2e7d32, #4caf50)',
                backgroundClip: 'text',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent'
              }}>
                Healthcare Provider Dashboard
              </Typography>
            </Fade>
            
            <Fade in={true} timeout={1200}>
              <Typography variant="h6" color="text.secondary" paragraph>
                Manage patients, appointments, and medical records
              </Typography>
            </Fade>
            
            <Grid container spacing={3} sx={{ mt: 4 }}>
              <Grid item xs={12} sm={6} md={3}>
                <Fade in={true} timeout={1400}>
                  <Card sx={{ 
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      transform: 'translateY(-8px)',
                      boxShadow: '0 8px 25px rgba(46, 125, 50, 0.2)',
                    }
                  }}>
                    <CardContent>
                      <People sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
                      <Typography variant="h4" component="div">
                        156
                      </Typography>
                      <Typography color="text.secondary">
                        Active Patients
                      </Typography>
                    </CardContent>
                  </Card>
                </Fade>
              </Grid>
              
              <Grid item xs={12} sm={6} md={3}>
                <Fade in={true} timeout={1600}>
                  <Card sx={{ 
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      transform: 'translateY(-8px)',
                      boxShadow: '0 8px 25px rgba(46, 125, 50, 0.2)',
                    }
                  }}>
                    <CardContent>
                      <Schedule sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
                      <Typography variant="h4" component="div">
                        23
                      </Typography>
                      <Typography color="text.secondary">
                        Today's Appointments
                      </Typography>
                    </CardContent>
                  </Card>
                </Fade>
              </Grid>
              
              <Grid item xs={12} sm={6} md={3}>
                <Fade in={true} timeout={1800}>
                  <Card sx={{ 
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      transform: 'translateY(-8px)',
                      boxShadow: '0 8px 25px rgba(46, 125, 50, 0.2)',
                    }
                  }}>
                    <CardContent>
                      <Assignment sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
                      <Typography variant="h4" component="div">
                        8
                      </Typography>
                      <Typography color="text.secondary">
                        Pending Reports
                      </Typography>
                    </CardContent>
                  </Card>
                </Fade>
              </Grid>
              
              <Grid item xs={12} sm={6} md={3}>
                <Fade in={true} timeout={2000}>
                  <Card sx={{ 
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      transform: 'translateY(-8px)',
                      boxShadow: '0 8px 25px rgba(46, 125, 50, 0.2)',
                    }
                  }}>
                    <CardContent>
                      <LocalHospital sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
                      <Typography variant="h4" component="div">
                        12
                      </Typography>
                      <Typography color="text.secondary">
                        Emergency Cases
                      </Typography>
                    </CardContent>
                  </Card>
                </Fade>
              </Grid>
            </Grid>
            
            <Fade in={true} timeout={2200}>
              <Box sx={{ mt: 4, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Button 
                  variant="contained" 
                  startIcon={<People />}
                  sx={{
                    background: 'linear-gradient(45deg, #2e7d32, #4caf50)',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      transform: 'translateY(-2px)',
                      boxShadow: '0 6px 20px rgba(46, 125, 50, 0.4)',
                    }
                  }}
                >
                  View Patients
                </Button>
                <Button 
                  variant="outlined" 
                  startIcon={<Schedule />}
                  sx={{
                    borderColor: '#2e7d32',
                    color: '#2e7d32',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      borderColor: '#1b5e20',
                      backgroundColor: 'rgba(46, 125, 50, 0.04)',
                      transform: 'translateY(-2px)',
                    }
                  }}
                >
                  Manage Appointments
                </Button>
                <Button 
                  variant="outlined" 
                  startIcon={<Assignment />}
                  sx={{
                    borderColor: '#2e7d32',
                    color: '#2e7d32',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      borderColor: '#1b5e20',
                      backgroundColor: 'rgba(46, 125, 50, 0.04)',
                      transform: 'translateY(-2px)',
                    }
                  }}
                >
                  Medical Records
                </Button>
                <Button 
                  variant="outlined"
                  sx={{
                    borderColor: '#2e7d32',
                    color: '#2e7d32',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      borderColor: '#1b5e20',
                      backgroundColor: 'rgba(46, 125, 50, 0.04)',
                      transform: 'translateY(-2px)',
                    }
                  }}
                >
                  Reports & Analytics
                </Button>
              </Box>
            </Fade>
          </Box>
        </Slide>
      </Container>
    </Box>
  );
};

export default Dashboard; 