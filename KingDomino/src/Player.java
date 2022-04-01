import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;




public class Player {
    private String name;
    private Color color;
    private Tile[][] playerGrid;
    private int territorySize = 0;

    public Player(String name, Color color, Tile[][] playerGrid){
        this.name = name;
        this.color = color;
        this.playerGrid = playerGrid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Tile[][] getPlayerGrid() {
        return this.playerGrid;
    }

    public void setGrid(Tile[][] playerGrid) {
        this.playerGrid = playerGrid;
    }
    //enable = true --- disable = flase
    public void setPlayerGrid(Boolean bool){
        for (int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                if (playerGrid[x][y].getOccupied() == true){
                    playerGrid[x][y].setEnabled(false);
                }
                else { playerGrid[x][y].setEnabled(bool);}
            }
        }
    }
    public void enableNeighbour(ArrayList<Integer> coordinate){
        ArrayList<ArrayList<Integer>> neighbouList = AddNeighbours(coordinate);
        for (int x=0; x<5; x++){
            for (int y=0; y<5; y++){
                ArrayList<Integer> arr = new ArrayList<>();
                arr.add(x);
                arr.add(y);
                if (playerGrid[x][y].getOccupied() == false && neighbouList.contains(arr)){
                    playerGrid[x][y].setEnabled(true);
                }
                else{ playerGrid[x][y].setEnabled(false);}
            }
        }
    }

    

    private ArrayList<ArrayList<Integer>> AddNeighbours(ArrayList<Integer> selectedTile){
        ArrayList<ArrayList<Integer>> neighbourList = new ArrayList<>();
        int x = selectedTile.get(0);
        int y = selectedTile.get(1);
        AddCoordinate(neighbourList, x+1, y); // bottom square
        AddCoordinate(neighbourList, x-1, y); // top square
        AddCoordinate(neighbourList, x, y+1); // right square
        AddCoordinate(neighbourList, x, y-1); // left square
        return neighbourList;
    }

    private void AddCoordinate(ArrayList<ArrayList<Integer>> neighbourList, int x, int y){
        //making sure the neighbouring squares are on the 5x5 grid
        if ((x >= 0 & x <= 4) & (y >= 0 & y <= 4 )){  
            //making an an ArrayList object that holds the coordinates of the square
            //System.out.println("x:"+x +" y: "+y);
            ArrayList<Integer> cooordinate = new ArrayList<>();
            cooordinate.add(x);
            cooordinate.add(y);
            //adding the neighbouring square to the respective neighbouring ArrayList
            neighbourList.add(cooordinate);
        }
    }
    public void setTileColorWhite(){
        for(int x=0; x<5; x++){
            for (int y=0; y<5; y++){
                if (x == 2 & y == 2) {continue;} //skip the king tile
                playerGrid[x][y].setBackground(Color.white);
            }
        }
    }
    public boolean isPlacementValid(Tile tile1, Tile tile2){
        ArrayList<ArrayList<Integer>> tile1Neighbours = AddNeighbours(tile1.getCoord());
        tile1Neighbours.remove(tile2.getCoord());
        ArrayList<ArrayList<Integer>> tile2Neighbours = AddNeighbours(tile2.getCoord());
        tile2Neighbours.remove(tile1.getCoord());

        //System.out.print("List1 :\n");
        //PrintArray(tile1Neighbours);
        //System.out.print("List2 :\n");
        //PrintArray(tile2Neighbours);

        

        boolean result1 = checkTerrain(tile1Neighbours, tile1);
        boolean result2 = checkTerrain(tile2Neighbours, tile2);
        return result1 || result2;

    }
    private boolean checkTerrain(ArrayList<ArrayList<Integer>> tileNeighbours, Tile tile){
        ArrayList<Integer> king_tile = new ArrayList<>();
        king_tile.add(2);
        king_tile.add(2);
        if (tileNeighbours.contains(king_tile)) {return true;} //next to king tile
        String terrain1 = tile.getTerrain(); 
        for (ArrayList<Integer> element:tileNeighbours){
            System.out.println(Arrays.toString(element.toArray()));
            String terrain = this.playerGrid[element.get(0)][element.get(1)].getTerrain();
            //System.out.println(terrain);
            if (terrain1.equals(terrain)) {return true;}
        }
        return false;
    }
    private void PrintArray(ArrayList<ArrayList<Integer>> list){
        for (ArrayList<Integer> element: list){
            System.out.println(Arrays.toString(element.toArray()));
        }
    }
    public void createCastle(){
        //creating the King tile 
        playerGrid[2][2].setBackground(color);
        playerGrid[2][2].placeCastle();
        playerGrid[2][2].setEnabled(false);
    }
    
    public ArrayList<Tile> getNeighboursList(Tile tile){
    	ArrayList<Tile> neighbours = new ArrayList<>();
    	ArrayList<Integer> coords = new ArrayList<>();
    	int xcoord = tile.getXcoord();
    	int ycoord = tile.getYcoord();
    	coords.add(xcoord);
    	coords.add(ycoord);
    	for (ArrayList<Integer> neighbourCoord : AddNeighbours(coords)) {
    		int neighbourX = neighbourCoord.get(0);
    		int neighbourY = neighbourCoord.get(1);
    		if (tile.getTerrain().equals(playerGrid[neighbourX][neighbourY].getTerrain())) {
    			neighbours.add(playerGrid[neighbourX][neighbourY]);
    		}
    	}
    	return neighbours;
    	
    }
    
    public int cascadeScore(Tile tile) {
    	int scoreCount = 0;
    	for (Tile neighbour: getNeighboursList(tile)) {
    		if (!neighbour.getCounted()) {
    			territorySize += 1;
    			scoreCount += neighbour.getCrowns();
    			neighbour.setCounted();
    			return scoreCount + cascadeScore(neighbour);
    		}
    	}
		return scoreCount;
    }
    
    public int calculateScore() {
    	int scoreCount = 0;
    	int totalScore = 0;
    	for (Tile[] tileRow : playerGrid) {
    		for (Tile tile : tileRow) {
    			territorySize = 0;
    			if (!tile.getCounted()) {
    				territorySize += 1;
    				tile.setCounted();
    				scoreCount += tile.getCrowns() + cascadeScore(tile);
    				scoreCount = territorySize * scoreCount;
    				totalScore += scoreCount;
    			}
    		}
    	}
		return totalScore;
    }

}
